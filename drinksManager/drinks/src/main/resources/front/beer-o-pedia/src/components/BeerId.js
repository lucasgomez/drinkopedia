import React, { Component } from 'react';
import {
  Well,
  Label,
  Grid,
  Table,
  Row,
  Col
} from 'react-bootstrap';
import RatedLabel from './RatedLabel';

class BeerId extends Component {
  constructor(props: any) {
    super(props);

    this.state = {
      beer: null,
      isLoading: false
    };
  }

  componentDidMount() {
    this.fetchData(this.props.beerId);
  }

  componentWillReceiveProps(nextProps) {
    debugger;
    let newBeerId = nextProps.beerId;
    let oldBeerId = this.props.beerId;
    if(newBeerId !== oldBeerId) {
       this.fetchData(newBeerId);
    }
  }

  fetchData = (beerId) => {
    this.setState({
      isLoading: true
    });

    let baseUrl = 'http://localhost:8081/drinkopedia/beers/' + beerId;

    fetch(baseUrl)
      .then(response => response.json())
      .then(body => body.entity)
      .then(item =>
        this.setState({
          beer: item,
          isLoading: false
        })
      );
  }

  render() {
    const {
      beer,
      isLoading
    } = this.state;

    if (isLoading || !beer) {
      return <div class="container"><p> Loading... < /p></div>;
    }

    return (
      <div class="container">

        <Grid>
          <Row>
            <Col xs={12} md={6}>
              <h2><a href='beerId' id='beerId'>{beer.name}</a></h2>
              <p>{beer.producerName + " (" + beer.producerOriginName + ")" }</p>
            </Col>
            <Col xs={12} md={6}>
            </Col>
          </Row>

          <Row>
            <Col xs={12} md={6}>
              <BasicProperties beer={beer}/>
            </Col>
            <Col xs={12} md={6}>
              <BeerRadar beer={beer}/>
            </Col>
          </Row>

          <Row>
            <Col xs={12} md={12}>
              <BeerDescription beer={beer}/>
            </Col>
          </Row>

          <Row>
            <Col xs={3} md={3}>
              <BarsService bars={beer.bottleBars} beer={beer}/>
              <BarsService bars={beer.tapBars} beer={beer}/>
            </Col>
          </Row>
        </Grid>
      </div>
    );

  }
}

const BarsService = (props) => {
  debugger;
  if (props.bars)
    return (
      <div>
        {props.bars.map((bar: any) =>
            <BottleBarService beer={props.beer} bar={bar}/>
        )}
      </div>
    );
  return <div/>;
}

const BottleBarService = (props) => (
  <Well>
    <h3>{props.bar.name}</h3>
    <p>{props.bar.comment}</p>
    <PriceDisplay beer={props.beer}/>
  </Well>
)

const PriceDisplay = (props) => {
  debugger;
  if (props.beer.bottleVolumeInCl)
    return <h4>{props.beer.bottleVolumeInCl+"cl "+props.beer.bottleSellingPrice+".-"}</h4>;
  if (props.beer.tapPriceSmall != null)
    return <h4><div>{"30cl "+props.beer.tapPriceSmall+".-"}</div><div>{"50cl "+props.beer.tapPriceSmall+".-"}</div></h4>;
  debugger;
}

const BasicProperties = (props) => (
  <Well>
    <Table>
      <Row>
        <NamedLabel name="Alcool" value={props.beer.abv}/>
        <NamedLabel name="Couleur" value={props.beer.colorName}/>
      </Row><Row>
        <NamedLabel name="Style" value={props.beer.styleName}/>
        <NamedLabel name="Fermentation" value={props.beer.fermenting}/>
      </Row>
    </Table>
  </Well>
)

const BeerRadar = (props) => (
  <Well>
    <Table>
      <RatedLabel name="Amertume" strength={props.beer.bitternessRank}/>
      <RatedLabel name="Acidité" strength={props.beer.sournessRank}/>
      <RatedLabel name="Douceur" strength={props.beer.sweetnessRank}/>
      <RatedLabel name="Houblonnage" strength={props.beer.hoppingRank}/>
    </Table>
  </Well>
)

const BeerDescription = (props) => (
  <Well>
    <p>{props.beer.comment}</p>
  </Well>
)

const NamedLabel = (props) => (
  <div>
    {
      props.value ?
        <div><Col md={6}><Label>{props.name}</Label></Col>
        <Col md={6}>{props.value}</Col></div>
      :
        <div><Col md={6}></Col>
        <Col md={6}></Col></div>
    }
  </div>
);

export default BeerId;
