import React from "react";

import { formatDate, isDate } from "../../../../../../utilities/date-utilities";
import { Icon, Popover } from "antd";
import { IconInfoCircle } from "../../../../../../components/icons/Icons";
import { grey1 } from "../../../../../../styles/colors";

/**
 * Component to properly display dates in the ag-grid
 */
export class DateCellRenderer extends React.Component {
  render() {
    const content = <div>{i18n("linelist.dateCell.tooltip")}</div>;
    if (!this.props.value) {
      return "";
    } else if (isDate(this.props.value)) {
      return formatDate({ date: this.props.value });
    } else {
      return (
        <div style={{ display: "flex" }}>
          <span style={{ flex: 1 }}>{this.props.value}</span>
          <Popover
            content={content}
            title={
              <span>
                <Icon type="exclamation-circle-o" />{" "}
                {i18n("linelist.dateCell.popover.title")}
              </span>
            }
          >
            <IconInfoCircle style={{ color: grey1 }} />
          </Popover>
        </div>
      );
    }
  }
}
