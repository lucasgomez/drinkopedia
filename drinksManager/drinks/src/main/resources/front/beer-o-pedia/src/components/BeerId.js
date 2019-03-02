import React, { Component } from 'react';
import {
  Card,
  Container,
  Table,
  Row,
  Col
} from 'react-bootstrap';
import { Link } from 'react-router-dom';
import RatedLabel from './RatedLabel';
import { API_ROOT } from '../data/apiConfig';
import { withAuth } from '@okta/okta-react';
import fetch from 'isomorphic-fetch';

class BeerId extends Component {
  constructor(props: any) {
    super(props);

    this.state = {
      beer: null,
      answer: null,
      isLoading: false
    };
  }

  componentDidMount() {
    this.fetchData(this.props.beerId);
  }

  componentWillReceiveProps(nextProps) {
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

    let answerUrl = `${API_ROOT}/private/beers/godmode`;

    fetch(answerUrl, {
            headers: {
              Authorization: 'Bearer ' + this.props.auth.getAccessToken()
            }
          })
        .then(response => response.json())
        .then(data =>
          this.setState({answer: data})
        );
    //
    // const response = await fetch(answerUrl, {
    //         headers: {
    //           Authorization: 'Bearer ' + await this.props.auth.getAccessToken()
    //         }
    //       });
    // const data = await response.json();
    // this.setState({
    //   answer: data
    // });

    let beerUrl = `${API_ROOT}/public/beers/` + beerId;

    fetch(beerUrl)
      .then(response => response.json())
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
      answer,
      isLoading
    } = this.state;

    if (isLoading || !beer) {
      return <div class="container"><p> Loading... < /p></div>;
    }

    return (
      <div class="container">
        <Container>
          <Row>
            <Col xs={6} md={6}>
              <h2>{beer.name}</h2>
              <h4>
              <Link to={'/list/producers/'+beer.producerId}>{beer.producerName}</Link>
              {' - '}
              <Link to={'/list/origins/'+beer.producerOriginId}>{beer.producerOriginName}</Link>
              </h4>
            </Col>
          </Row>

          <Row>
            <Col xs={12} md={4}>
              <BeerDescription beer={beer}/>
            </Col>
            <Col xs={12} md={4}>
              <BasicProperties beer={beer}/>
            </Col>
            <Col xs={12} md={4}>
              <BeerRadar beer={beer}/>
            </Col>
          </Row>

          <Row>
            <BarsService bars={beer.bottleBars} beer={beer} type="bottle"/>
            <BarsService bars={beer.tapBars} beer={beer} type="tap"/>
          </Row>

          <Row>
            Et la grande réponse est : {answer}
          </Row>
        </Container>
      </div>
    );

  }
}

const BarsService = (props) => {
  if (props.bars)
    return (
      <div>
        {props.bars.map((bar: any) =>
          <Col xs={6} md={4}>
            <BarServiceDetails beer={props.beer} bar={bar} type={props.type}/>
          </Col>
        )}
      </div>
    );
  return <div/>;
}

const BarServiceDetails = (props) => (
  <Card body>
    <h4>{<Link to={'/list/bars/'+props.bar.id}>{props.bar.name}</Link>}</h4>
    <p>{props.bar.comment}</p>
    <PriceDisplay beer={props.beer} type={props.type}/>
  </Card>
)

const PriceDisplay = (props) => {
  if (props.type === "bottle")
    return (
      <Table striped>
        <Row>
          <Col xs={6}><h5>{props.beer.bottleVolumeInCl+"cl"}</h5></Col>
          <Col xs={6}><h5>{props.beer.bottleSellingPrice+".-"}</h5></Col>
        </Row>
      </Table>);
  if (props.type === "tap")
    return (
      <Table striped>
        <Row>
          <Col xs={6}><h5>{"30cl"}</h5></Col>
          <Col xs={6}><h5>{props.beer.tapPriceSmall+".-"}</h5></Col>
        </Row>
        <Row>
          <Col xs={6}><h5>{"50cl"}</h5></Col>
          <Col xs={6}><h5>{props.beer.tapPriceBig+".-"}</h5></Col>
        </Row>
      </Table>);
  return <div/>;
}

const BasicProperties = (props) => (
  <Card body>
    <Table>
      <NamedLabel name="Alcool" value={props.beer.abv+' %'}/>
      <NamedLabel name="Couleur" value={<Link to={'/list/colors/'+props.beer.colorId}>{props.beer.colorName}</Link>}/>
      <NamedLabel name="Style" value={<Link to={'/list/styles/'+props.beer.styleId}>{props.beer.styleName}</Link>}/>
      <NamedLabel name="Fermentation" value={props.beer.fermenting}/>
    </Table>
  </Card>
)

const NamedLabel = (props) => (
  <div>
    {
      props.value ?
        <Row>
          <Col md={6} xs={6}><b>{props.name}</b></Col>
          <Col md={6} xs={6}>{props.value}</Col>
        </Row>
      :
        <Row>
          <Col md={6} xs={6}></Col>
          <Col md={6} xs={6}></Col>
        </Row>
    }
  </div>
);

const BeerRadar = (props) => (
  <Card body>
    <Table>
      <RatedLabel name="Amertume" strength={props.beer.bitternessRank}/>
      <RatedLabel name="Acidité" strength={props.beer.sournessRank}/>
      <RatedLabel name="Douceur" strength={props.beer.sweetnessRank}/>
      <RatedLabel name="Houblonnage" strength={props.beer.hoppingRank}/>
    </Table>
  </Card>
)

const BeerDescription = (props) => (
  <Card body>
    <h4>Description</h4>
    <p>{props.beer.comment}</p>
  </Card>
)

export default withAuth(BeerId);
