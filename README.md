# Seemingly Equivalent Firm Decision Heuristics

### Documentation for branch StrategyExploration

This branch contains the code for the paper "Seemingly Equivalent Firm Decision Heuristics" initially presented at CEF 2016 and later refined and submitted to the Internation Journal of Microsimulation.

Visit [my website](http://meissereconomics.com/2016/06/28/Heuristics.html) to download the CEF 2016 version of the paper and the presentation slides. I will also link to the Journal version once it is published.

To reproduce the various findings of the model, run the following classes:

- [StabilityProfiles](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/StabilityProfiles.java) to obtain output [StabilityProfiles.out](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/StabilityProfiles.out) which constitutes the raw data of the three stability profiles in the paper.
- [TimeSeries](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/TimeSeries.java) to obtain output [TimeSeries.out](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/TimeSeries.out) which contains the raw data for the time series of the discussed individual simulation runs.
- [ParameterExploration2](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/ParameterExploration2.java) to get [ParameterExploration2.out](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/ParameterExploration2.out), which shows that the simulation stays reasonably stable for returns to scale up to 0.9
- [HeuristicsCompetition](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/HeuristicsCompetition.java) to get [HeuristicsCompetition.out](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/HeuristicsCompetition.out), which represents the microconsistency test that shows that there is an incentive to deviate.


###General instructions

In order to modify and run the model yourself, you should follow these steps:

1. Make sure a git client is installed. I am using SourceTree: https://www.sourcetreeapp.com/
2. Make sure an IDE for Java is installed. I am using eclipse for Java EE developers, obtainable from https://eclipse.org/downloads/ .
3. Add this repository (https://github.com/meissereconomics/agentecon.git) and checkout the right branch.
4. Import the project to eclipse. In case you are using a different IDE, make sure to add jar\agenteconinterface.jar to the classpath.
5. Run the class you are interested in.