import React from "react";
import PropTypes from "prop-types";

/**
 * Formats and displays the filtered counts for the table.
 */
export function FilteredCounts(props) {
  return (
    <span tour="tour-filter-counts">
      {__("linelist.infobar.filterCounts")
        .replace("[FILTER_COUNT]", props.filterCount)
        .replace("[TOTAL]", props.totalSamples)}
    </span>
  );
}

FilteredCounts.propTypes = {
  filterCount: PropTypes.number,
  totalSamples: PropTypes.number
};
