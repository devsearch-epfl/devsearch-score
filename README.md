# devsearch-score

Our scoring function will be applied to a list of unordered search results and will score each item. This allows us to sort the results such that the best matches will appear on the top of the list.


## Idea
With the help of a machine-learned ranking algorithm we will find out, how much each superfeature (such as the number of matching features, RepoRank, the locality of matching features, etc.) shall be weighted in order to score each search result. 
