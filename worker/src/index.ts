import { createRemoteJWKSet, jwtVerify } from "jose";

export interface Env {
  /** Set via: wrangler secret put SHOPIFY_ADMIN_API_ACCESS_TOKEN */
  SHOPIFY_ADMIN_API_ACCESS_TOKEN: string;
  FIREBASE_PROJECT_ID: string;
  FIREBASE_PROJECT_NUMBER: string;
  SHOPIFY_ADMIN_ENDPOINT: string;
}

class HttpError extends Error {
  constructor(public status: number, message: string) {
    super(message);
  }
}

// jose caches these remote key sets (and respects their Cache-Control) across
// requests handled by the same isolate, so this isn't a fetch-per-request cost.
const FIREBASE_AUTH_JWKS = createRemoteJWKSet(
  new URL(
    "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com"
  )
);

const APP_CHECK_JWKS = createRemoteJWKSet(
  new URL("https://firebaseappcheck.googleapis.com/v1/jwks")
);

/**
 * Verifies the caller is a real, currently-signed-in Firebase user.
 * See: https://firebase.google.com/docs/auth/admin/verify-id-tokens
 */
async function requireFirebaseUser(request: Request, env: Env): Promise<string> {
  const header = request.headers.get("Authorization") ?? "";
  const match = header.match(/^Bearer (.+)$/);
  if (!match) throw new HttpError(401, "Missing bearer token.");

  try {
    const { payload } = await jwtVerify(match[1], FIREBASE_AUTH_JWKS, {
      issuer: `https://securetoken.google.com/${env.FIREBASE_PROJECT_ID}`,
      audience: env.FIREBASE_PROJECT_ID,
    });
    if (!payload.sub) throw new Error("Missing sub claim.");
    return payload.sub;
  } catch {
    throw new HttpError(401, "Invalid or expired ID token.");
  }
}

/**
 * Verifies the request came from a genuine, unmodified build of the app.
 * See: https://firebase.google.com/docs/app-check/custom-resource-backend
 */
async function requireAppCheck(request: Request, env: Env): Promise<void> {
  const token = request.headers.get("X-Firebase-AppCheck");
  if (!token) throw new HttpError(401, "Missing App Check token.");

  try {
    await jwtVerify(token, APP_CHECK_JWKS, {
      issuer: `https://firebaseappcheck.googleapis.com/${env.FIREBASE_PROJECT_NUMBER}`,
      audience: `projects/${env.FIREBASE_PROJECT_NUMBER}`,
    });
  } catch {
    throw new HttpError(401, "Invalid or expired App Check token.");
  }
}

async function callShopifyAdmin(
  env: Env,
  query: string,
  variables: Record<string, unknown>
): Promise<Response> {
  const shopifyResponse = await fetch(env.SHOPIFY_ADMIN_ENDPOINT, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Shopify-Access-Token": env.SHOPIFY_ADMIN_API_ACCESS_TOKEN,
    },
    body: JSON.stringify({ query, variables }),
  });

  const json = await shopifyResponse.text();
  return new Response(json, {
    status: shopifyResponse.ok ? 200 : 502,
    headers: { "Content-Type": "application/json" },
  });
}

// ---- GraphQL mutations, unchanged from the app's previous client-side code ----

const DRAFT_ORDER_CREATE = `
  mutation draftOrderCreate($input: DraftOrderInput!) {
    draftOrderCreate(input: $input) {
      draftOrder {
        id
        invoiceUrl
        subtotalPriceSet { shopMoney { amount currencyCode } }
        totalTaxSet { shopMoney { amount currencyCode } }
        totalShippingPriceSet { shopMoney { amount currencyCode } }
        totalPriceSet { shopMoney { amount currencyCode } }
      }
      userErrors { field message }
    }
  }
`;

const DRAFT_ORDER_COMPLETE = `
  mutation draftOrderComplete($id: ID!, $paymentPending: Boolean) {
    draftOrderComplete(id: $id, paymentPending: $paymentPending) {
      draftOrder {
        id
        order { id name }
      }
      userErrors { field message }
    }
  }
`;

const ORDER_MARK_AS_PAID = `
  mutation orderMarkAsPaid($input: OrderMarkAsPaidInput!) {
    orderMarkAsPaid(input: $input) {
      order { id name }
      userErrors { field message }
    }
  }
`;

const ORDER_CANCEL = `
  mutation orderCancel(
    $orderId: ID!,
    $notifyCustomer: Boolean,
    $refund: Boolean!,
    $restock: Boolean!,
    $reason: OrderCancelReason!
  ) {
    orderCancel(
      orderId: $orderId,
      notifyCustomer: $notifyCustomer,
      refund: $refund,
      restock: $restock,
      reason: $reason
    ) {
      job { id done }
      orderCancelUserErrors { field message }
      userErrors { field message }
    }
  }
`;

type RouteHandler = (body: Record<string, unknown>, env: Env) => Promise<Response>;

const ROUTES: Record<string, RouteHandler> = {
  "/draftOrderCreate": async (body, env) => {
    const input = body.input;
    if (!input || typeof input !== "object") {
      throw new HttpError(400, "Missing draft order input.");
    }
    return callShopifyAdmin(env, DRAFT_ORDER_CREATE, { input });
  },

  "/draftOrderComplete": async (body, env) => {
    const id = body.id;
    if (!id || typeof id !== "string") {
      throw new HttpError(400, "Missing draft order id.");
    }
    return callShopifyAdmin(env, DRAFT_ORDER_COMPLETE, {
      id,
      paymentPending: Boolean(body.paymentPending),
    });
  },

  "/orderMarkAsPaid": async (body, env) => {
    const id = body.id;
    if (!id || typeof id !== "string") {
      throw new HttpError(400, "Missing order id.");
    }
    return callShopifyAdmin(env, ORDER_MARK_AS_PAID, { input: { id } });
  },

  "/orderCancel": async (body, env) => {
    const orderId = body.orderId;
    if (!orderId || typeof orderId !== "string") {
      throw new HttpError(400, "Missing order id.");
    }
    return callShopifyAdmin(env, ORDER_CANCEL, {
      orderId,
      notifyCustomer: Boolean(body.notifyCustomer),
      refund: true,
      restock: true,
      reason: "CUSTOMER",
    });
  },
};

function errorResponse(status: number, message: string): Response {
  return new Response(JSON.stringify({ error: message }), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    try {
      if (request.method !== "POST") {
        throw new HttpError(405, "Method not allowed.");
      }

      const handler = ROUTES[new URL(request.url).pathname];
      if (!handler) throw new HttpError(404, "Not found.");

      // Both checks must pass before we ever touch the Shopify admin token.
      await requireFirebaseUser(request, env);
      await requireAppCheck(request, env);

      const body = (await request.json().catch(() => ({}))) as Record<string, unknown>;
      return await handler(body, env);
    } catch (err) {
      if (err instanceof HttpError) {
        return errorResponse(err.status, err.message);
      }
      return errorResponse(500, "Internal error.");
    }
  },
};
