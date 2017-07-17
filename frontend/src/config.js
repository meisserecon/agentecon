export default {
  apiURL: 'http://192.168.79.102:8080',
  xhrConfig: {
    mode: 'cors',
  },
  handleFetchErrors(response) {
    if (!response.ok) {
      throw Error(response.statusText);
    }
    return response;
  },
  alertError(error) {
    /* eslint-disable no-alert */
    alert(error);
    /* eslint-enable no-alert */
  },
};
