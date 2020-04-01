import axios from "axios";
import { Alert, notification, Progress, Tooltip } from "antd";
import { IconCloseCircle } from "../../components/icons/Icons";
import React from "react";
import { SPACE_XS } from "../../styles/spacing";
import { blue6 } from "../../styles/colors";

/**
 * Upload a list of files to the server.
 *
 * @param {array} files - List of files to upload
 * @param {string} url - Url to upload to
 * @param {function} onProgressUpdate - allows caller to tap into the upload progress.
 * @returns {Promise<AxiosResponse<any>>}
 */
export function uploadFiles({ files, url, onProgressUpdate = () => {} }) {
  const names = files.map(f => f.name);
  const formData = new FormData();
  files.forEach((f, i) => formData.append(`files[${i}]`, f));

  const CancelToken = axios.CancelToken;
  const source = CancelToken.source();
  const key = Date.now();

  const showProgressNotification = ({
    progress,
    duration = 0,
    cancelled = false
  }) =>
    notification.info({
      key,
      style: { width: 400 },
      closeIcon: <span />,
      message: i18n("FileUploader.progress.title"),
      description: (
        <>
          {i18n("FileUploader.progress.desc")}
          {
            <ul className="t-file-upload">
              {names.map(name => (
                <li key={name}>{name}</li>
              ))}
            </ul>
          }
          {cancelled ? (
            <Alert
              message={i18n("FileUploader.progress.cancelled")}
              type="info"
            />
          ) : (
            <>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  marginBottom: SPACE_XS
                }}
              >
                <Progress
                  style={{ flexGrow: 1 }}
                  percent={progress}
                  size="small"
                />
                <Tooltip
                  title={i18n("FileUploader.progress.tooltip")}
                  placement="topRight"
                >
                  <IconCloseCircle
                    style={{ marginLeft: 5, color: blue6 }}
                    onClick={cancelUpload}
                  />
                </Tooltip>
              </div>
              <Alert
                message={`Don't refresh your page or the download will be cancelled.`}
                type="warning"
              />
            </>
          )}
        </>
      ),
      placement: "bottomRight",
      duration
    });

  const cancelUpload = () => {
    showProgressNotification({ progress: 0, duration: 4, cancelled: true });
    source.cancel();
  };

  /**
   * Function called when window onbeforeunload is called when a file is
   * uploading, since leaving the page would cause the upload to cancel.
   * This prompts the user if they want to continue leaving the site.
   * @param event
   */
  const listener = event => {
    // Cancel the event as stated by the standard.
    event.preventDefault();
    // Chrome requires returnValue to be set.
    event.returnValue = window.confirm(i18n("FileUploader.listener-warning"));
  };

  window.addEventListener("beforeunload", listener);

  return axios
    .post(url, formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      },
      cancelToken: source.token,
      onUploadProgress: function(progressEvent) {
        const totalLength = progressEvent.lengthComputable
          ? progressEvent.total
          : progressEvent.target.getResponseHeader("content-length") ||
            progressEvent.target.getResponseHeader(
              "x-decompressed-content-length"
            );
        if (totalLength !== null) {
          const progress = Math.round(
            (progressEvent.loaded * 100) / totalLength
          );
          showProgressNotification({ progress });
        }
      }
    })
    .then(({ data }) => data)
    .catch(({ data }) => data)
    .finally(() => {
      window.removeEventListener("beforeunload", listener);
    });
}