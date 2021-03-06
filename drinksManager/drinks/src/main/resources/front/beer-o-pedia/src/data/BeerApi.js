import React, { Component } from 'react';

class BeerApi extends Component {
  fetchList = async (listName, listId) => {
    this.setState({
      isLoading: true
    });

    const baseUrl = 'http://localhost:8081/drinkopedia/beers/';
    let listUrl = baseUrl + listName + '/' + listId;

    fetch(listUrl)
      .then(response => response.json())
      .then(list =>
        this.setState({
          items: list.beers,
          title: list.name,
          description: list.description,
          isLoading: false
        })
      );
  }
}

export default BeerApi;
