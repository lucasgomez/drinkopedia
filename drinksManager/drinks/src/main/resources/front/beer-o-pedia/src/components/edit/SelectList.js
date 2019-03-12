import React, { Component } from 'react';
import { API_ROOT } from '../../data/apiConfig';
import { ReactstrapSelect } from "reactstrap-formik";
import { Field } from 'formik';

class SelectList extends Component {
  constructor(props: any) {
    super(props);

    this.state = {
      items: [],
      producerOptions: [{id:1, name:"India"}, {id:2, name:"USA"}, {id:166, name:"UK"}, {id:4, name:"Saudi Arabia"}],
      isLoading: false
    };
  }

  componentDidMount() {
    this.setState({
      isLoading: true
    });
    let baseUrl = `${API_ROOT}/public/lists/` + this.props.listName;

    fetch(baseUrl)
      .then(response => response.json())
      .then(items =>
        this.setState({
          items: items,
          isLoading: false
        })
      );
  }

  render() {
    const {
      items,
      producerOptions,
      isLoading
    } = this.state;
    const name = this.props.name;
    const label = this.props.label;
    const tab = Array.from(["India", "USA", "UK", "Saudi Arabia"]);

    debugger;
    if (isLoading) {
      return (
        <div>Loading...</div>
      );
    }

    return (
      <Field
        label={label}
        name={name}
        component={ReactstrapSelect}
        inputprops={{
          name: {name},
          id: {name},
          options: items,
          defaultOption: "-"
        }}
      />
    );
  }
}

export default SelectList;
