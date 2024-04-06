import React, { useEffect, useState } from "react";
import { Table, Card } from "antd";
import { Resizable } from "react-resizable";
import styles from './style/index.module.less';

const ResizableTitle = (props) => {
  const { onResize, width, ...restProps } = props;
  if (!width) {
    return <th {...restProps} />;
  }

  return (
    <Resizable
      width={width}
      height={0}
      handle={
        <span
          className="react-resizable-handle"
          onClick={(e) => {
            e.stopPropagation();
          }}
        />
      }
      onResize={onResize}
      draggableOpts={{
        enableUserSelectHack: false,
      }}
    >
      <th {...restProps} />
    </Resizable>
  );
};

const ResizeableTable = (props) => {

  const [columns, setColumns] = useState(props.columns);
  useEffect(() => {
    setColumns(props.columns);
  }, [props.columns]);

  const handleResize = (index) => {
    return (_, { size }) => {
      const newColumns = [...columns];
      newColumns[index] = {
        ...newColumns[index],
        width: size.width,
      };
      setColumns(newColumns);
    };
  };

  const mergeColumns = columns.map((col, index) => {
    return {
      ...col,
      ellipsis: col.ellipsis === false ? col.ellipsis : true,
      onHeaderCell: (column) => ({
        width: column.width,
        onResize: handleResize(index),
      }),
    };
  });

  return (
    <div className={styles['resize-table']}>
      <Table
        {...props}
        components={{
          header: {
            cell: ResizableTitle,
          },
        }}
        columns={mergeColumns}
        showSorterTooltip={false}
        // pagination={
        //   props.Pagination
        //     ? {
        //       size: "default",
        //       showSizeChanger: true,
        //       total: props.totalCounts,
        //       pageSizeOptions: [5, 10, 20],
        //       onChange: (a) => props.onChange?.(a),
        //       current: props.currentPage,
        //       pageSize: props.currentPageSize,
        //     }
        //     : false
        // }
        rowSelection={props.rowSelection}
      />
    </div>
  );
};

export default ResizeableTable;
