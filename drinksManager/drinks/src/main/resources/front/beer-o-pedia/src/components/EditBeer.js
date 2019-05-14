import React, { Component } from 'react';
import * as yup from 'yup';
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { ReactstrapInput, ReactstrapSelect } from "reactstrap-formik";
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
      tap: null,
      bottle: null,
      fireRedirect: false,
      isLoadingBeer: false,
      isLoadingTap: false,
      isLoadingBottle: false,
    };

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleTapSubmit = this.handleTapSubmit.bind(this);
    this.handleBottleSubmit = this.handleBottleSubmit.bind(this);
  }

  componentDidMount() {
    this.fetchData(this.props.beerId);
  }

  fetchData = (beerId) => {
    this.setState({
      isLoadingBeer: true,
      isLoadingTap: true,
      isLoadingBottle: true,
    });

    let beerUrl = '/private/beers/' + beerId;
    let tapBeerUrl = beerUrl + '/tap';
    let bottleBeerUrl = beerUrl + '/bottle';

    var self = this;

    //Fetch "basic" beer data
    axios.get(
      beerUrl,
      { withCredentials: true })
    .then(function (response) {
       self.setState({
         beer: response.data,
         isLoadingBeer: false
       })
     })
    .catch(function (error) {
       console.log(error);
    });

    //Fetch "tap" data
    axios.get(
      tapBeerUrl,
      { withCredentials: true })
    .then(function (response) {
      self.setState({
        tap: response.data,
        isLoadingTap: false
      })
    })
    .catch(function (error) {
      console.log(error);
    });

    //Fetch "bottle" data
    axios.get(
      bottleBeerUrl,
      { withCredentials: true })
    .then(function (response) {
      self.setState({
        bottle: response.data,
        isLoadingBottle: false
      })
    })
    .catch(function (error) {
      console.log(error);
    });

  }

//TODO 3x duplicated submit function
  handleSubmit(values, {setSubmitting}) {
    let updatedBeer = Object.assign(this.state.beer, values);
    let postBeerUrl = '/private/beers/' + updatedBeer.id;

    var self = this;
    axios.put(
      postBeerUrl,
      updatedBeer,
      { withCredentials: true }
    ).then(function (response){
       setSubmitting(false);
       self.setState({
         fireRedirect: true
       });
     }).catch(function (error) {
       setSubmitting(false);
       console.log(error);
       alert("Ebriété assumée, erreur assurée");
     }
    );

  }

  handleTapSubmit(values, {setSubmitting}) {
    let updatedTap = Object.assign(this.state.tap, values);
    let postTapUrl = '/private/beers/' + this.state.beer.id + '/tap';

    var self = this;
    axios.put(
      postTapUrl,
      updatedTap,
      { withCredentials: true })
    .then(function (response){
       setSubmitting(false);
       self.setState({
         fireRedirect: true
       });
     }).catch(function (error) {
       setSubmitting(false);
       console.log(error);
       alert("Ebriété assumée, erreur assurée");
     }
    );

  }

  handleBottleSubmit(values, {setSubmitting}) {
    let updatedBottle = Object.assign(this.state.bottle, values);
    let postBottleUrl = `/private/beers/` + this.state.beer.id + '/bottle';

    var self = this;
    axios.put(
      postBottleUrl,
      updatedBottle,
      { withCredentials: true })
    .then(function (response){
       setSubmitting(false);
       self.setState({
         fireRedirect: true
       });
     }).catch(function (error) {
       setSubmitting(false);
       console.log(error);
       alert("Ebriété assumée, erreur assurée");
     }
    );

  }

  render() {

    const {
      beer,
      isLoadingBeer,
      isLoadingTap,
      isLoadingBottle,
      fireRedirect
    } = this.state;

    const redirectUrl = '/beerid/'+this.props.beerId;

    if (isLoadingBeer || isLoadingTap || isLoadingBottle  || !beer) {
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
            comment: this.state.beer.comment,
            abv: this.state.beer.abv,
            producerId: this.state.beer.producerId,
            styleId: this.state.beer.styleId,
            colorId: this.state.beer.colorId,
            bitternessRank: this.state.beer.bitternessRank,
            sournessRank: this.state.beer.sournessRank,
            sweetnessRank: this.state.beer.sweetnessRank,
            hoppingRank: this.state.beer.hoppingRank,
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

       <Formik
           initialValues={{
            buyingPricePerLiter: this.state.tap.buyingPricePerLiter,
            priceBig: this.state.tap.priceBig,
            priceSmall: this.state.tap.priceSmall,
            availability: this.state.tap.availability,
            barsIds: this.state.tap.barsIds,
           }}
           onSubmit={this.handleTapSubmit}

           render={({ submitForm, isSubmitting, values, handleReset, dirty }) => (

                <Form>
                  <Field
                    label="Disponibilité"
                    name="availability"
                    component={ReactstrapSelect}
                    inputprops={{
                      name: "availability",
                      id: "availability",
                      options: ['NOT_YET_AVAILABLE', 'AVAILABLE', 'NEARLY_OUT_OF_STOCK', 'OUT_OF_STOCK'],
                      defaultOption: "NOT_YET_AVAILABLE"
                    }}
                  />
                  < Field
                  id = "tapBuyingPricePerLiter"
                  type = "text"
                  label = "Prix d'achat (CHF/L)"
                  name = "buyingPricePerLiter"
                  component = {
                    ReactstrapInput
                  }
                  /> <
                  ErrorMessage name = "buyingPricePerLiter" / >

                  <
                  Field
                  id = "tapPriceSmall"
                  type = "text"
                  label = "Prix de vente (25cL)"
                  name = "priceSmall"
                  component = {
                    ReactstrapInput
                  }
                  />
                  <ErrorMessage name = "priceSmall" / >

                  <
                  Field
                  id = "tapPriceBig"
                  type = "text"
                  label = "Prix de vente (50cl)"
                  name = "priceBig"
                  component = {
                    ReactstrapInput
                  }
                  /> <
                  ErrorMessage name = "priceBig" / >


                  <BarsCheckboxes groupName="barsIds"/>

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

       <Formik
           initialValues={{
             buyingPrice: this.state.bottle.buyingPrice,
             sellingPrice: this.state.bottle.sellingPrice,
             volumeInCl: this.state.bottle.volumeInCl,
             availability: this.state.bottle.availability,
             barsIds: this.state.bottle.barsIds,
           }}
           onSubmit={this.handleBottleSubmit}

           render={({ submitForm, isSubmitting, values, handleReset, dirty }) => (

              <Form>
                <Field
                  label="Disponibilité"
                  name="availability"
                  component={ReactstrapSelect}
                  inputprops={{
                    name: "availability",
                    id: "availability",
                    options: ['NOT_YET_AVAILABLE', 'AVAILABLE', 'NEARLY_OUT_OF_STOCK', 'OUT_OF_STOCK'],
                    defaultOption: "NOT_YET_AVAILABLE"
                  }}
                />

                <Field
                id="bottleBuyingPrice"
                type="text"
                label="Prix d'achat bouteille"
                name="buyingPrice"
                component={ReactstrapInput}
                />
                <ErrorMessage name="buyingPrice" />

                <Field
                id="bottleSellingPrice"
                type="text"
                label="Prix de vente bouteille"
                name="sellingPrice"
                component={ReactstrapInput}
                />
                <ErrorMessage name="sellingPrice" />

                <Field
                id="bottleVolumeInCl"
                type="text"
                label="Volume bouteille (cL)"
                name="volumeInCl"
                component={ReactstrapInput}
                />
                <ErrorMessage name="volumeInCl" />

                <BarsCheckboxes groupName="barsIds"/>

                 <button
                     type="button"
                     className="outline"
                     onClick={handleReset}
                     disabled={!dirty || isSubmitting}>
                     <Emoji symbol="✖" label="Annuler"/> Annuler
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
    .nullable()
    .min(0)
    .max(100)
});

export default EditBeer;
