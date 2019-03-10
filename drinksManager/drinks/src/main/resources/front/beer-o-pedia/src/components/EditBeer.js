import React, { Component } from 'react';
import * as yup from 'yup';
import { Formik, FormikProps, Field, Form, ErrorMessage } from 'formik';
import { ReactstrapRadio, ReactstrapSelect } from "reactstrap-formik";
import { API_ROOT } from '../data/apiConfig';

class EditBeer extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      name: null,
      description: null,
      bitternessRank: null,
      sournessRank: null,
      sweetnessRank: null,
      hoppingRank: null,
      isLoading: false
    };

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
          sournessRank: beer.sournessRank,
          sweetnessRank: beer.sweetnessRank,
          hoppingRank: beer.hoppingRank,
          isLoading: false
        })
      );
  }

  handleSubmit(values, {setSubmitting}) {
    setTimeout(() => {
          setSubmitting(false);
          alert(
            `Submitted Successfully ->  ${JSON.stringify(values, null, 2)}`
          );
        }, 1000);
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
          sournessRank: this.state.sournessRank,
          sweetnessRank: this.state.sweetnessRank,
          hoppingRank: this.state.hoppingRank,
        }}
        validationSchema={beerValidator}
        onSubmit={this.handleSubmit}

        render={({ submitForm, isSubmitting, values }) => (

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

                <StrengthInput name="bitternessRank" label="Amertume"/>
                <br/>

                <StrengthInput name="sournessRank" label="Acidité"/>
                <br/>

                <StrengthInput name="sweetnessRank" label="Douceur"/>
                <br/>

                <StrengthInput name="hoppingRank" label="Houblonnage"/>
                <br/>

                <button
                  type="submit"
                  disabled={isSubmitting}>
                     Submit Form
                </button>
              </Form>
       )}
      />
    );
  }
}

const StrengthInput = (props) => (
  <div>
    <label htmlFor={props.name} style={{ display: 'block' }}>
      {props.label}
    </label>
    <Field
        name={props.name}
        component={ReactstrapRadio}
        value=""
        type="radio"
        label="-"
      />
    <Field
        name={props.name}
        component={ReactstrapRadio}
        value="1"
        type="radio"
        label="1"
      />
    <Field
        name={props.name}
        component={ReactstrapRadio}
        value="2"
        type="radio"
        label="2"
      />
    <Field
        name={props.name}
        component={ReactstrapRadio}
        value="3"
        type="radio"
        label="3"
      />
    </div>
)

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
