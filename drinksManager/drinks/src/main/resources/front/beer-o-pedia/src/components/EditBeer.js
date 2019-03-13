import React, { Component } from 'react';
import * as yup from 'yup';
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { ReactstrapInput } from "reactstrap-formik";
import SelectList from './edit/SelectList';
import StrengthInput from './edit/StrengthInput';
import { API_ROOT } from '../data/apiConfig';

class EditBeer extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      name: null,
      producerId: null,
      abv: null,
      styleId: null,
      colorId: null,
      description: null,
      bitternessRank: null,
      sournessRank: null,
      sweetnessRank: null,
      hoppingRank: null,
      producerOptions: [{id:1, name:"India"}, {id:2, name:"USA"}, {id:166, name:"UK"}, {id:4, name:"Saudi Arabia"}],
      isLoading: false,
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
          producerId: beer.producerId,
          abv: beer.abv,
          styleId: beer.styleId,
          colorId: beer.colorId,
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
          producerId: this.state.producerId,
          abv: this.state.abv,
          styleId: this.state.styleId,
          colorId: this.state.colorId,
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
                <Field
                  id="name"
                  type="text"
                  label="Nom"
                  name="name"
                  placeholder="Nom de la Bière"
                  component={ReactstrapInput}
                  />
                  <ErrorMessage name="name" />
                <br/>

                <SelectList label="Producteur" name="producerId" listName="producers"/>
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
                  id="abv"
                  type="text"
                  label="Alcool (%)"
                  name="abv"
                  component={ReactstrapInput}
                  />
                <ErrorMessage name="abv" />
                <br/>

                <SelectList label="Couleur" name="colorId" listName="colors"/>
                <br/>

                <SelectList label="Style" name="styleId" listName="styles"/>
                <br/>

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

const beerValidator = yup.object().shape({
  name: yup
    .string()
    .trim()
    .required("Le nom est obligatoire"),
  description: yup
    .string()
    .trim()
    .max(255, "Description trop longue"),
  abv: yup
    .number()
    .min(0)
    .max(100)
});

export default EditBeer;
