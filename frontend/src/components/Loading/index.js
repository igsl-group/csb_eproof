import "./style/index.css";

if (!document.getElementById('global-loading-root')) {
  const loadingRoot = document.createElement('div');
  loadingRoot.setAttribute('id', 'global-loading-root');
  loadingRoot.classList.add("global-loading-root")
  loadingRoot.classList.add("hidden");

  const loadingContainer = document.createElement('div');
  loadingContainer.className = "global-loading-container";
  loadingRoot.appendChild(loadingContainer);

  const loadingItem = document.createElement('div');
  loadingItem.className = "global-loading-item loading";
  loadingContainer.appendChild(loadingItem);

  document.body.appendChild(loadingRoot);
}

let requestCount = 0

const showLoading  = () => {
  if (requestCount === 0) {
    const dom = document.getElementById('global-loading-root');
    dom.classList.remove("hidden");
  }
  requestCount++
}

const hideLoading = () => {
  requestCount--
  if (requestCount === 0) {
    const dom = document.getElementById('global-loading-root');
    dom.classList.add("hidden");
  }
}

export {
  showLoading,
  hideLoading,
}