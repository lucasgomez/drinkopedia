import React, { Component } from 'react';
import {
  Table,
  Row, Col, Card,
  Tooltip,
  OverlayTrigger
} from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { API_ROOT } from '../data/apiConfig';


class TapBeersDisplay extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      items: [],
      title: "",
      description: "",
      isLoading: false
    };
  }

  componentDidMount() {
    this.fetchData(this.props.listName, this.props.listId);
  }

  componentWillReceiveProps(nextProps) {
    let oldListId = this.props.listId;
    let newListId = nextProps.listId;
    let oldListName = this.props.listName;
    let newListName = nextProps.listName;
    if (oldListId !== newListId || oldListName !== newListName) {
         this.fetchData(newListName, newListId);
    }
  }

  fetchData = async (listName, listId) => {
    this.setState({
      isLoading: true
    });

    let listUrl = `${API_ROOT}/public/beers/`;
    if (listName && listId)
      listUrl += listName + '/' + listId;

    fetch(listUrl)
      .then(response => response.json())
      .then(list =>
        this.setState({
          items: list.beers,
          title: list.name,
          isLoading: false
        })
      );
  }

  render() {
    const {
      items,
      title,
      isLoading
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }
    return (
      <div class="container-fluid">
        <Row>
          <h1 class="col text-center">🍺 Pressions - Liste de prix (0.25 / 0.5 cL) 🍻</h1>
        </Row>
          {items.map((beer, index) =>
            {
              index%4==0 ? "<Row>" : "pas mu"
            }
              <Col>
        				<Card body>
      						<h4 class="card-title">{beer.name}<div class="float-right"><h5>{beer.tapPriceSmall}.- / {beer.tapPriceBig}.-</h5></div></h4>
      						<h7 class="card-subtitle mb-2 text-muted">{beer.producerName} ({beer.producerOriginName})</h7>
      						<div>
      							{beer.abv}% <i>{beer.styleName}</i> ({beer.colorName})
      							<h4 class="float-sm-right">🌟</h4>
      						</div>
        				</Card>
              </Col>
            {
              index%4==3 ? "</Row>" : "pas mu"
            }
          )}
      </div>
    );
  }
}

export default TapBeersDisplay;
