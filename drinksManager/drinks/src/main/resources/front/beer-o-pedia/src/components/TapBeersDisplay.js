import React, { Component } from 'react';
import {
  Row, Col, Card, Container,
} from 'react-bootstrap';
import Emoji from './Emoji';
import { API_ROOT } from '../data/apiConfig';


class TapBeersDisplay extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      items: [],
      title: "",
      description: "",
      updateTime: null,
      isLoading: false
    };
  }

  componentDidMount() {
    this.fetchData(this.props.listName, this.props.listId);

    this.interval = setInterval(this.tick, 1000*60*2);
  }

  componentWillUnmount() {
     clearInterval(this.interval);
  }

  tick = () => {
    this.fetchData(this.state.listName, this.state.listId);
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
          items: list.beers.filter(beer => beer.tapAvailability && !['NOT_YET_AVAILABLE', 'OUT_OF_STOCK'].includes(beer.tapAvailability)),
          title: list.name,
          listName: listName,
          listId: listId,
          updateTime: new Date,
          isLoading: false
        })
      );
  }

  renderProducer = (beer) => {
    let producerString = beer.producerName
    if (beer.producerOriginShortName)
      producerString += " ("+beer.producerOriginShortName+")"

    return producerString
  }

  renderAvailibity = (beer) => {
    if (beer.tapAssortment=="FIXED")
      return null;

    switch (beer.tapAvailability) {
      case "AVAILABLE":
        return <Emoji symbol="⌛" label="Assortiment fixe"/>;
      case "NEARLY_OUT_OF_STOCK":
        return <Emoji symbol="⏳" label="Bientôt épuisée"/>;
      default:
        return null;
    }
  }

  createBeerCard = (beer, isPair) => {
    let bgColor=isPair?"":"dark"
    let textColor=isPair?"":"light"
    return (
      <Col>
        <Card body bg={bgColor} text={textColor}>
          <h4 class="card-title">{beer.name}<div class="float-right"><h5>{beer.tapPriceSmall}.- / {beer.tapPriceBig}.-</h5></div></h4>
          <h7 class="card-subtitle mb-2 text-muted">{this.renderProducer(beer)}</h7>
          <div>
            {beer.abv}% <i>{beer.styleName}</i> ({beer.colorName})
            <h4 class="float-sm-right">{this.renderAvailibity(beer)}</h4>
          </div>
        </Card>
      </Col>
    );
  }

  createTable = (items, rowLength) => {
    let table = []

    for (let rowId = 0; rowId < (items.length/rowLength); rowId++) {
      let children = []

      for (let colId = 0; colId < rowLength; colId++) {
        let isEven = (colId+rowId)%2==0
        let cellId = (rowId*rowLength)+colId

        if (cellId<items.length)
          children.push(this.createBeerCard(items[cellId], isEven))
        else
          children.push(<Col/>)
      }
      table.push(<Row noGutters="true">{children}</Row>)
    }
    return table
  }

  render() {
    const {
      items,
      isLoading,
      updateTime
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }
    return (
      <Container fluid="true">
        <Row>
          <h1 class="col text-center"><Emoji symbol="🍺" label="Choppe"/> Pressions - Liste de prix (25 / 50 cL) <Emoji symbol="🍻" label="Choppes faisant santé"/></h1>
        </Row>

        {this.createTable(items, 4)}

        <p>{updateTime && 'MàJ : '+('0'+updateTime.getHours()).slice(-2)+":"+('0'+updateTime.getMinutes()).slice(-2)}</p>
    		<Row className="text-center">
    			<h5 class="col"><Emoji symbol="🌟" label="Assortiment fixe"/> - Assortiment fixe</h5>
    			<h5 class="col"><Emoji symbol="🚨" label="Nouvellement ajoutée"/> - Nouvellement ajoutée</h5>
    			<h5 class="col"><Emoji symbol="⏱" label="Assortiment temporaire"/> - Assortiment temporaire</h5>
    			<h5 class="col"><Emoji symbol="⏳" label="Bientôt épuisée"/> - Bientôt épuisée</h5>
    		</Row>
      </Container>
    );
  }
}

export default TapBeersDisplay;
