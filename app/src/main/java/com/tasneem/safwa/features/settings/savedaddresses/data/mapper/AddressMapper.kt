package com.tasneem.safwa.features.settings.savedaddresses.data.mapper

import com.apollographql.apollo.api.Optional
import com.tasneem.safwa.features.settings.savedaddresses.data.model.AddressCacheDto
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.network.fragment.CustomerAddressFields
import com.tasneem.safwa.network.type.MailingAddressInput


 fun CustomerAddressFields.toDomain(isDefault: Boolean): Address = Address(
    id = id,
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    phone = phone.orEmpty(),
    street = address1.orEmpty(),
    apartment = address2.orEmpty(),
    city = city.orEmpty(),
    province = province.orEmpty(),
    zip = zip.orEmpty(),
    countryName = country.orEmpty(),
    countryCode = countryCodeV2?.rawValue.orEmpty(),
    isDefault = isDefault
)

 fun Address.toInput(): MailingAddressInput = MailingAddressInput(
    address1 = Optional.present(street),
    address2 = Optional.present(apartment.ifBlank { null }),
    city = Optional.present(city),
    country = Optional.present(countryName),
    firstName = Optional.present(firstName),
    lastName = Optional.present(lastName),
    phone = Optional.present(phone),
    province = Optional.present(province.ifBlank { null }),
    zip = Optional.present(zip)
)

 fun Address.toCacheDto() = AddressCacheDto(
    id,
    firstName,
    lastName,
    phone,
    street,
    apartment,
    city,
    province,
    zip,
    countryName,
    countryCode,
    isDefault
)

 fun AddressCacheDto.toDomain() = Address(
    id, firstName, lastName, phone, street, apartment, city, province, zip, countryName, countryCode, isDefault
)