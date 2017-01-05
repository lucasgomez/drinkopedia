Feature: Basic access to Drinkopedia service tests
 
Scenario: Empty drink repositories returns 204
Given the drink repositories are empty
 When I load all drinks
 Then it should return code 204
 
Scenario: Get Drinks on repositories with data returns 200
Given the drink repositories with sample drinks
 When I load all drinks
 Then it should return code 200

Scenario: A drink deleted among existing drinks
Given the drink repositories with sample drinks
 When I load all drinks
  And I delete the first drink
  And I load all drinks
 Then the collection of drinks lacks the deleted one

Scenario: A drink search by existing drink type
Given the drink repositories with sample drinks
 When I search for drinks of type 'beer'
 Then I get all drinks whose property type is 'beer'

Scenario: A drink search by non-existing drink type 'hydrazine'
Given the drink repositories with sample drinks
 When I search for drinks of type 'hydrazine'
 Then I get 404 response code

Scenario: A drink search by existing name
Given the drink repositories with sample drinks
 When I search for drinks with name like 'ale'
 Then I get a list of drinks whose name contains the chars 'ale'