import React, { Component } from 'react';
import * as yup from 'yup';
import { Formik, FormikProps, Field, Form, ErrorMessage } from 'formik';
import { ReactstrapRadio } from "reactstrap-formik";
import { API_ROOT } from '../data/apiConfig';

class EditBeer extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      name: null,
      description: null,
      bitternessRank: null,
      isLoading: false
    };

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidMount() {
    this.fetchData(this.props.beerId);
  }

  fetchData = (beerId) => {
    this.setState({
      isLoading: true
    });

    let beerUrl = `${API_ROOT}/public/beers/` + beerId;

    fetch(beerUrl)
      .then(response => response.json())
      .then(beer =>
        this.setState({
          name: beer.name,
          description: beer.comment,
          bitternessRank: beer.bitternessRank,
          isLoading: false
        })
      );
  }

  handleSubmit(event) {
    alert('Name: ' + this.state.name + " combo: " + this.state.bitternessRank + " description: '" + this.state.description + "'");
    event.preventDefault();
  }

  handleInputChange(event) {
    debugger;
    const target = event.target;
    const value = target.value;
    const name = target.name;
    this.setState({
      [name]: value
    });
  }

  render() {

    const {
      isLoading
    } = this.state;

    if (isLoading) {
      return <div class="container"><p> Loading... < /p></div>;
    }

    return (


      <Formik
        initialValues={{
          name: this.state.name,
          description: this.state.description,
          bitternessRank: this.state.bitternessRank,
        }}
        validationSchema={beerValidator}
        onSubmit={this.handleSubmit}
        render={formProps => {

           return(
              <Form>
                <label htmlFor="name" style={{ display: 'block' }}>
                  Nom
                </label>
                <Field
                  type="text"
                  name="name"
                  placeholder="Nom de la Bière"
                  />
                  <ErrorMessage name="name" />
                <br/>

                <label htmlFor="description" style={{ display: 'block' }}>
                  Description
                </label>
                <Field
                  component="textarea"
                  rows="4"
                  name="description"
                  placeholder="Texte de description de la Bière"
                  />
                <ErrorMessage name="description" />

                <Field
                    name="bitternessRank"
                    component={ReactstrapRadio}
                    value="1"
                    type="radio"
                    label="True"
                  />
                <Field
                    name="bitternessRank"
                    component={ReactstrapRadio}
                    value="2"
                    type="radio"
                    label="False"
                  />
                <Field
                    name="bitternessRank"
                    component={ReactstrapRadio}
                    value="3"
                    type="radio"
                    label="False"
                  />
                <br/>
                <button
                  type="submit"
                  disabled={formProps.isSubmitting}>
                     Submit Form
                </button>
              </Form>
           );
       }}
      />
    );
  }
}

const beerValidator = yup.object().shape({
  name: yup
    .string()
    .trim()
    .required("Le nom est obligatoire"),
  description: yup
    .string()
    .trim()
    .max(255, "Description trop longue"),
});

export default EditBeer;
