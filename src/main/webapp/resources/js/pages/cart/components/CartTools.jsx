import React from "react";
import { Location, Router } from "@reach/router";
import { Row } from "antd";
import { COLOURS, SPACING } from "../../../styles";
import styled from "styled-components";
import { Pipelines } from "../../../components/pipelines/Pipelines";
import { getI18N } from "../../../utilities/i18n-utilties";
import { CartToolsMenu } from "./CartToolsMenu";

const ToolsWrapper = styled(Row)`
  height: 100%;
  width: 100%;
  background-color: ${COLOURS.BG_LIGHTEST};
  position: relative;
`;

const ToolsInner = styled.div`
  padding: ${SPACING.DEFAULT};
  position: absolute;
  top: 50px;
  right: 0;
  bottom: 0;
  left: 0;
  overflow-x: auto;
`;

/**
 * Wrapper component for functionality available in the cart.
 */
export class CartTools extends React.Component {
  /*
   * Update here to add new tab items to the page.
   */
  paths = [
    {
      key: "/cart/pipelines",
      link: "cart/pipelines",
      text: getI18N("CartTools.menu.pipelines"),
      component: (
        <Pipelines key="cart/pipelines" path="cart/pipelines" default />
      )
    }
  ];

  render() {
    return (
      <ToolsWrapper>
        <Location>
          {({ location }) => (
            <>
              <CartToolsMenu pathname={location.pathname} paths={this.paths} />
              <ToolsInner>
                <Router>{this.paths.map(path => path.component)}</Router>
              </ToolsInner>
            </>
          )}
        </Location>
      </ToolsWrapper>
    );
  }
}
