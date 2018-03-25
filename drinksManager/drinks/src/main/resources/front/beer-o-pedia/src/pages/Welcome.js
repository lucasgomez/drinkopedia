import React, { Component } from 'react';
import {Bootstrap, ButtonGroup, Button, Tabs, Tab} from 'react-bootstrap';
import ButtonsList from './../components/ButtonsList';
import BeersList from './../components/BeersList';


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
      <div class="container">
    		<Tabs>
    			<Tab id="listsTab" eventKey="1" title={ 'Les cartes de Bière' }>
    				<p>{ 'Tu trouveras ci-dessous les listes de nos Bières classées :' }</p>
    				<ButtonGroup vertical>
    					<Button>{ 'Par nom' }</Button>
    					<Button>{ 'Par bar' }</Button>
    					<ButtonsList listName="colors" title="Par couleur" handleItemClick={this.handleShowList}/>
    					<ButtonsList listName="styles" title="Par style" handleItemClick={this.handleShowList}/>
    					<ButtonsList listName="producers" title="Par producteur" handleItemClick={this.handleShowList}/>
    					<ButtonsList listName="places" title="Par origine" handleItemClick={this.handleShowList}/>
    				</ButtonGroup>
    			</Tab>
      		<Tab id="dataTab" eventKey="2" title={ 'En savoir plus' }>
      			<p>{ 'Qui n\'a jamais rêvé de tout savoir sur la Bière? Désormais, c\'est possible!' }</p>
            <BeersList listName="styles" title="IPA" description="Les Indian Pale Ale (IPA), c'est bon. Si si! C'est bon" listId="14"/>
    			</Tab>
    		</Tabs>
      </div>
    );
  }

  handleSelect(selectedTab) {
    this.setState({
      activeTab: selectedTab
    });
  }

  //TODO [lg] Avoid creating a brand new callback instance on every render but use arrow init
  // handleShowList = (listName) => (id) => {
  handleShowList (listName, id) {
    debugger;
    alert('mu2 id:'+id+' list:'+listName);
    console.log('listName', listName);
    console.log('id', id);
  }
}

export default Welcome;
