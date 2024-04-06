import React, { useEffect, useState } from 'react';
import styles from './style/index.module.less';
import {useNavigate, useParams, Link} from "react-router-dom";
import {Col, Form, Card, Typography, Breadcrumb, Input, Button, Space, Tag, Row, Flex} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  SearchOutlined,
  FilterOutlined,
  DownloadOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";

export default function LicenceTypes(props) {

  return (
    <div className={styles['users']}>
      <Typography.Title level={3}>Licence Types</Typography.Title>
      <Breadcrumb>
        <Breadcrumb.Item><HomeOutlined /></Breadcrumb.Item>
        <Breadcrumb.Item>Licence Types</Breadcrumb.Item>
      </Breadcrumb>
    </div>
  )
}
