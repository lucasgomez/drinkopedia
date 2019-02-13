import React, { Component } from 'react';
import {
  Row,
  Col
} from 'react-bootstrap';

class RatedLabel extends Component {

  render() {
    const {
      name,
      strength
    } = this.props;
    return (
      <Row>
        <Col md={6} xs={6}><strong>{name}</strong></Col>
        <Col md={6} xs={6}><b>{this.displayStars(strength)}</b></Col>
      </Row>
    );

  }

  displayStars(strength) {

    const emptyStar = (<i className="fa fa-beer" style={{color:'lightgrey'}}></i>);
    const checkedStar = (<i className="fa fa-beer checked" style={{color:'green'}}></i>);
    const uncheckedStar = (<i className="fa fa-beer" style={{color:'black'}}></i>);

    let rating = [];
    let i = 0;
    if (strength == null || strength === "0") {
      for (i = 0; i < 3; i++) {
        rating.push(emptyStar);
      }
    } else {
      for (i = 0; i < strength; i++) {
        rating.push(checkedStar);
      }
      for (i = 0; i < 3-strength; i++) {
        rating.push(uncheckedStar);
      }
    }
    return rating;
  }
}

export default RatedLabel;
