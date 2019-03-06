import React, { Component } from 'react';
import {
  Container,
  Col,
  Form
} from 'react-bootstrap';
import { API_ROOT } from '../data/apiConfig';

// const EditBeer = () => (
class EditBeer extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      name: null,
      description: null,
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
          isLoading: false
        })
      );
  }

  handleSubmit(event) {
    alert('Name: ' + this.state.name + " description: '" + this.state.description + "'");
    event.preventDefault();
  }

  handleInputChange(event) {
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
      <form onSubmit={this.handleSubmit}>
        <label>
          Name:
          <input
            name="name"
            type="text"
            value={this.state.name}
            onChange={this.handleInputChange} />
        </label>
        <br />

        <label>
          Description:
          <textarea
            name="description"
            value={this.state.description}
            onChange={this.handleInputChange} />
        </label>
        <input type="submit" value="Submit" />
      </form>
    );
  }
}

export default EditBeer;
