import React, { useState } from "react";
import { render } from "react-dom";
import { Layout, Menu, PageHeader, Typography } from "antd";
import { BasicList } from "../../../../components/lists";
import {
  MetadataTemplateProvider,
  useMetadataTemplate,
} from "../../../../contexts/metadata-template-context";
import { IconLoading } from "../../../../components/icons/Icons";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { Template } from "./Template";
import { Link, Router } from "@reach/router";
import Sider from "antd/es/layout/Sider";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const { Content } = Layout;
const { Paragraph } = Typography;

const TemplateDetails = () => {
  const { template, loading, updateField } = useMetadataTemplate();

  return (
    <BasicList
      dataSource={[
        {
          title: "TEMPLATE NAME",
          desc: (
            <Paragraph
              editable={
                window.project.canManage && !loading
                  ? {
                      onChange: (value) => updateField("name", value),
                    }
                  : null
              }
            >
              {loading ? <IconLoading /> : template.name}
            </Paragraph>
          ),
        },
        {
          title: "CREATED DATE",
          desc: loading ? (
            <IconLoading />
          ) : (
            formatInternationalizedDateTime(template.createdDate)
          ),
        },
      ]}
    />
  );
};

function MetadataTemplatePage() {
  const { template, loading } = useMetadataTemplate();
  const { activeKey, setActiveKey } = useState("details");
  const defaultHref = window.location.href;
  const [, projectId, templateId] = defaultHref.match(
    /\w+\/(\d+)\/[a-z-]+\/(\d+)/
  );
  console.log(projectId, templateId);

  const BASE_URL = setBaseUrl(
    `/projects/${projectId}/metadata-template/${templateId}`
  );
  return (
    <PageHeader
      className="site-page-header"
      onBack={() => null}
      title={loading ? "" : template.name}
    >
      <Layout>
        <Layout>
          <Sider width={200}>
            <Menu
              mode="inline"
              style={{ height: "100%", borderRight: 0, width: 200 }}
              selectedKeys={[activeKey]}
            >
              <Menu.Item key="details">
                <Link to={BASE_URL}>Template Details</Link>
              </Menu.Item>
              <Menu.Item key="fields">
                <Link to={`${BASE_URL}/fields`}>Metadata Fields</Link>
              </Menu.Item>
            </Menu>
          </Sider>
          <Content>
            <Router>
              <TemplateDetails path={BASE_URL} />
              <Template path={`${BASE_URL}/fields`} />
            </Router>
          </Content>
        </Layout>
      </Layout>
    </PageHeader>
  );
}

render(
  <MetadataTemplateProvider id={1}>
    <MetadataTemplatePage />
  </MetadataTemplateProvider>,
  document.querySelector("#root")
);
