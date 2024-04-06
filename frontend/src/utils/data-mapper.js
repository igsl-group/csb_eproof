
const dataMapperRequired = (dataMapper, type, key) => {
  return dataMapper[key]?.required.includes(type);
}

const dataMapperConvertPayload = (dataMapper, type, values) => {
  const payload = {};
  for (let key in values) {
    if (dataMapper[key]?.mapper.includes(type)) {
      if (typeof dataMapper[key]?.converter === "function") {
        payload[key] = dataMapper[key]?.converter(values[key]);
        continue;
      }
      payload[key] = values[key] || '';
    }
  }
  return payload;
}

export {
  dataMapperRequired,
  dataMapperConvertPayload
}