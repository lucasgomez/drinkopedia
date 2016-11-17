Feature: Basic access to Drinkopedia service tests
 
Scenario: Empty repository returns 204
Given the repository is empty
 When I load all drinks
 Then it should return code 204
 
Scenario: Get Drinks on repository with data returns 200
Given a drink repository with sample drinks
 When I load all drinks
 Then it should return code 200

Scenario: Post a new drink returns 201
Given the repository is empty
 When I post a new drink
 Then it should return code 201
 
Scenario: Newly posted drink can be retrieved
Given the repository is empty
 When I post a new drink
  And load the aforesaid drink at its location
 Then it should return the created drink
 
Scenario: A drink updated among existing drinks
Given a drink repository with sample drinks
 When a drink name is updated
  And the aforesaid drink is loaded
 Then that reloaded drink has the new name

Scenario: A drink deleted among existing drinks
Given a drink repository with sample drinks
 When I load all drinks
  And I delete the first drink
  And I load all drinks
 Then the collection of drinks lacks the deleted one

Scenario: A drink search by existing drink type
Given a drink repository with sample drinks
 When I search for drinks of type 'beer'
 Then I get all drinks whose property type is 'beer'

Scenario: A drink search by non-existing drink type 'hydrazine'
Given a drink repository with sample drinks
 When I search for drinks of type 'hydrazine'
 Then I get 404 response code

Scenario: A drink search by existing name
Given a drink repository with sample drinks
 When I search for drinks with name like 'ale'
 Then I get a list of drinks whose name contains the chars 'ale'