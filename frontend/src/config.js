export default {
  apiURL: 'http://192.168.79.102:8080',
  miniCharts: {
    noOfChartsInSidebar: 5,
    height: 300,
  },
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
    // eslint-disable-next-line no-alert
    alert(error);
  },
};
