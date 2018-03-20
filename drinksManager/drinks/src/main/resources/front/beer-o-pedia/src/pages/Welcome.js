import React, { Component } from 'react';
import {Bootstrap, ButtonGroup, Button, Tabs, Tab} from 'react-bootstrap';
import ButtonsList from './../components/ButtonsList';


class Welcome extends Component {
  constructor(props) {
	    super();
	    this.state = {
	      activeTab: props.activeTab || 1,
	    };

    this.handleSelect = this.handleSelect.bind(this);
  }

  render() {

    return (
		<Tabs>
			<Tab id="listsTab" eventKey="1" title={ 'Les cartes de Bière' }>
				<p>{ 'Tu trouveras ci-dessous les listes de nos Bières classées :' }</p>
				<ButtonGroup vertical>
					<Button>{ 'Par nom' }</Button>
					<Button>{ 'Par bar' }</Button>
					<ButtonsList listName="colors" title="Par couleur" handleItemClick={this.handleShowList}/>
					<ButtonsList listName="styles" title="Par style"/>
					<ButtonsList listName="producers" title="Par producteur"/>
					<ButtonsList listName="places" title="Par origine"/>
				</ButtonGroup>
			</Tab>
		<Tab id="dataTab" eventKey="2" title={ 'En savoir plus' }>
			<p>{ 'Qui n\'a jamais rêvé de tout savoir sur la Bière? Désormais, c\'est possible!' }</p>
			</Tab>
		</Tabs>
    );
  }

  handleSelect(selectedTab) {
    this.setState({
      activeTab: selectedTab
    });
  }

  handleShowList = (listName) => (id) => {
    alert("Mu");
    console.log('listName', listName);
    console.log('id', id);
  }
}

export default Welcome;
