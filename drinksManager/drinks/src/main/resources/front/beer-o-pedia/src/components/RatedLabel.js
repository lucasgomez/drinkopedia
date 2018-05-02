import React, { Component } from 'react';
import {
  Row,
  Label
} from 'react-bootstrap';
//import FontAwesome from '../react-fontawesome';

class RatedLabel extends Component {

  render() {
    const {
      name,
      strength
    } = this.props;
    return (
      <Row><Label>{name}</Label><div style={{inline:'block'}}>{this.displayStars(strength)}</div></Row>
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
