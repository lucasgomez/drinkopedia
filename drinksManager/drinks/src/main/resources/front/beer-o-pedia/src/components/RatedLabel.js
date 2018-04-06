import React, { Component } from 'react';
import {
  Label
} from 'react-bootstrap';
//import FontAwesome from '../react-fontawesome';

class RatedLabel extends Component {

  render() {
    const {
      name,
      strength
    } = this.props;
    debugger;

    return (
      <div><Label>{name}</Label>{this.displayStars(strength)}</div>
    );

  }

  displayStars(strength) {

    const emptyStar = (<span className="fa fa-beer" style="color:lightgrey"></span>);
    const checkedStar = (<i className="fa fa-beer checked"></i>);
    const uncheckedStar = (<i className="fa fa-beer"></i>);
    //const uncheckedStar = (<FontAwesome name="fa fa-beer"></FontAwesome>);
    debugger;
    let rating = [];
    let i = 0;
    if (strength === 0) {
      for (i = 0; i < 5; i++) {
        rating.push("-");
        // rating.push(emptyStar);
      }
    } else {
      for (i = 0; i < strength; i++) {
        rating.push("X");
        // rating.push(checkedStar);
      }
      for (i = 0; i < 5-strength; i++) {
        rating.push("O");
        // rating.push(uncheckedStar);
      }
    }
    // debugger;
    return (
      <div>
        {
          rating.map((star: any) => <div>{star}</div>)
        }
      </div>
    );
  }
}

export default RatedLabel;
