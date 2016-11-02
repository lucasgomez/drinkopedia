Feature: Basic access to Drinkopedia service tests
 
Scenario: Empty repository returns 204
Given the repository is empty
 When I load all drinks
 Then it should return code 204
 
Scenario: Get Drinks on repository with data returns 200
Given the repository has some content
 When I load all drinks
 Then it should return code 200