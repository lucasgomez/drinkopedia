import React, { Component } from 'react';
import * as yup from 'yup';
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { ReactstrapInput } from "reactstrap-formik";
import SelectList from './edit/SelectList';
import StrengthInput from './edit/StrengthInput';
import { API_ROOT } from '../data/apiConfig';
import { Link, Redirect } from 'react-router-dom';
import Emoji from './Emoji';
import BarsCheckboxes from './edit/BarsCheckboxes';
import axios from 'axios';

class EditBeer extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      beer: null,
      fireRedirect: false,
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

    let beerUrl = `${API_ROOT}/private/beers/` + beerId;

    var self = this;
    axios.get(beerUrl)
     .then(function (response) {
       self.setState({
         beer: response.data,
         isLoading: false
       })
     })
    .catch(function (error) {
       console.log(error);
    });
  }

  handleSubmit(values, {setSubmitting}) {
    let updatedBeer = Object.assign(this.state.beer, values);
    let postBeerUrl = `${API_ROOT}/private/beers/` + updatedBeer.id;

    var self = this;
    axios.put(postBeerUrl, updatedBeer)
     .then(function (response){
       setSubmitting(false);
       self.setState({
         fireRedirect: true
       });
     }).catch(function (error) {
       setSubmitting(false);
       console.log(error);
       alert("Erreur pas spécialement attendue, voir console pour détails");
     }
    );

  }

  render() {

    const {
      beer,
      isLoading,
      fireRedirect
    } = this.state;

    const redirectUrl = '/beerid/'+this.props.beerId;

    if (isLoading || !beer) {
      return <div class="container"><p> Loading... < /p></div>;
    }

    return (
      <div>
        <Link to={redirectUrl}>
          <h2>{'🔙'}</h2>
        </Link>

        {fireRedirect && (
          <Redirect to={redirectUrl}/>
        )}

        <Formik
          initialValues={{
            name: this.state.beer.name,
            producerId: this.state.beer.producerId,
            abv: this.state.beer.abv,
            styleId: this.state.beer.styleId,
            colorId: this.state.beer.colorId,
            comment: this.state.beer.comment,
            bitternessRank: this.state.beer.bitternessRank,
            sournessRank: this.state.beer.sournessRank,
            sweetnessRank: this.state.beer.sweetnessRank,
            hoppingRank: this.state.beer.hoppingRank,
            tapBuyingPricePerLiter: this.state.beer.tapBuyingPricePerLiter,
            tapPriceBig: this.state.beer.tapPriceBig,
            tapPriceSmall: this.state.beer.tapPriceSmall,
            bottleBuyingPrice: this.state.beer.bottleBuyingPrice,
            bottleSellingPrice: this.state.beer.bottleSellingPrice,
            bottleVolumeInCl: this.state.beer.bottleVolumeInCl,
            bottleBars: this.state.beer.bottleBars.map((bar: any) => bar.id),
          }}
          validationSchema={beerValidator}
          onSubmit={this.handleSubmit}

          render={({ submitForm, isSubmitting, values, handleReset, dirty }) => (

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

                  <label htmlFor="comment" style={{ display: 'block' }}>
                    Description
                  </label>
                  <Field
                    component="textarea"
                    rows="4"
                    name="comment"
                    placeholder="Texte de description de la Bière"
                    />
                  <ErrorMessage name="comment" />

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

                  <Field
                    id="tapBuyingPricePerLiter"
                    type="text"
                    label="Prix d'achat (CHF/L)"
                    name="tapBuyingPricePerLiter"
                    component={ReactstrapInput}
                    />
                  <ErrorMessage name="tapBuyingPricePerLiter" />

                  <Field
                    id="tapPriceBig"
                    type="text"
                    label="Prix de vente (50cl)"
                    name="tapPriceBig"
                    component={ReactstrapInput}
                    />
                  <ErrorMessage name="tapPriceBig" />

                  <Field
                    id="tapPriceSmall"
                    type="text"
                    label="Prix de vente (25cL)"
                    name="tapPriceSmall"
                    component={ReactstrapInput}
                    />
                  <ErrorMessage name="tapPriceSmall" />

                  <Field
                    id="bottleBuyingPrice"
                    type="text"
                    label="Prix d'achat bouteille"
                    name="bottleBuyingPrice"
                    component={ReactstrapInput}
                    />
                  <ErrorMessage name="bottleBuyingPrice" />

                  <Field
                    id="bottleSellingPrice"
                    type="text"
                    label="Prix de vente bouteille"
                    name="bottleSellingPrice"
                    component={ReactstrapInput}
                    />
                  <ErrorMessage name="bottleSellingPrice" />

                  <Field
                    id="bottleVolumeInCl"
                    type="text"
                    label="Volume bouteille (cL)"
                    name="bottleVolumeInCl"
                    component={ReactstrapInput}
                    />
                  <ErrorMessage name="bottleVolumeInCl" />

                  <BarsCheckboxes groupName="bottleBars"/>

                  <button
                      type="button"
                      className="outline"
                      onClick={handleReset}
                      disabled={!dirty || isSubmitting}>
                      ✖ Annuler
                    </button>
                  <button
                    type="submit"
                    disabled={isSubmitting}>
                       <Emoji symbol="💾" label="Sauver"/> Sauver
                  </button>
                </Form>
         )}
        />
      </div>
    );
  }
}

const beerValidator = yup.object().shape({
  name: yup
    .string()
    .trim()
    .required("Le nom est obligatoire"),
  comment: yup
    .string()
    .nullable()
    .trim()
    .max(255, "Description trop longue"),
  abv: yup
    .number()
    .min(0)
    .max(100)
});

export default EditBeer;
