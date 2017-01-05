Feature: Basic access to Drinkopedia service tests
 
Scenario: Empty repository returns 204
Given the repository is empty
 When I load all beers
 Then it should return code 204
 
Scenario: Get Drinks on repository with data returns 200
Given a beer repository with sample beers
 When I load all beers
 Then it should return code 200

Scenario: Post a new beer returns 201
Given the repository is empty
 When I post a new beer
 Then it should return code 201
 
Scenario: Newly posted beer can be retrieved
Given the repository is empty
 When I post a new beer
  And load the aforesaid beer at its location
 Then it should return the created beer
 
Scenario: A beer updated among existing beers
Given a beer repository with sample beers
 When a beer name is updated
  And the aforesaid beer is loaded
 Then that reloaded beer has the new name

Scenario: A beer deleted among existing beers
Given a beer repository with sample beers
 When I load all beers
  And I delete the first beer
  And I load all beers
 Then the collection of beers lacks the deleted one

Scenario: A beer search by existing name
Given a beer repository with sample beers
 When I search for beers with name like 'ale'
 Then I get a list of beers whose name contains the chars 'ale'