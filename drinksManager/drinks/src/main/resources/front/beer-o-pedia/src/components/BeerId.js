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

//TODO Make a MEGA GRID wrapping everithing as in mockup for BS and in column for SS
    return (
      <div class="container">
        <h2>{beer.name}</h2>
        <p>{beer.producerName + " (" + beer.producerOriginShortName + ")" }</p>

        <BasicProperties beer={beer}/>

        <BeerRadar beer={beer}/>

        <Well>
          <Grid>
            <Row className="show-grid">
              <Col>
                {"Amertume"}
              </Col>
              <Col>
                {beer.bitternessRank}
              </Col>
            </Row>

            <Row className="show-grid">
              <Col xs={6} md={4}>
                <code>&lt;{'Col xs={6} md={4}'} /">&gt;</code>
              </Col>
              <Col xs={6} md={4}>
                <code>&lt;{'Col xs={6} md={4}'} /">&gt;</code>
              </Col>
              <Col xsHidden md={4}>
                <code>&lt;{'Col xsHidden md={4}'} /">&gt;</code>
              </Col>
            </Row>

            <Row className="show-grid">
              <Col xs={6} xsOffset={6}>
                <code>&lt;{'Col xs={6} xsOffset={6}'} /">&gt;</code>
              </Col>
            </Row>

            <Row className="show-grid">
              <Col md={6} mdPush={6}>
                <code>&lt;{'Col md={6} mdPush={6}'} /">&gt;</code>
              </Col>
              <Col md={6} mdPull={6}>
                <code>&lt;{'Col md={6} mdPull={6}'} /">&gt;</code>
              </Col>
            </Row>
          </Grid>
        </Well>
      </div>
    );

  }
}

const BasicProperties = (props) => (
)

const BeerRadar = (props) => (
  <Well>
    <RatedLabel name="Amertume" strength={props.beer.bitternessRank}/>
    <RatedLabel name="Acidité" strength={props.beer.sournessRank}/>
    <RatedLabel name="Douceur" strength={props.beer.sweetnessRank}/>
    <RatedLabel name="Houblonnage" strength={props.beer.hoppingRank}/>
  </Well>
)

const NamedLabel = (props) => (
  <div>
    <Col md={6}>{props.name}</Col>
    <Col md={6}>{props.value}</Col>
  </div>
);

export default BeerId;
