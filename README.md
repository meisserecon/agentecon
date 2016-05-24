# Seemingly Equivalent Heuristics (#ExplorationChart)

This branch contains the code for the paper "Seemingly Equivalent Firm Decision Heuristics" presented at CEF 2016.

Run [com.agentecon.verification.ExplorationScenario](https://github.com/meisserecon/agentecon/blob/StrategyExploration/src/com/agentecon/verification/SimpleExplorationScenario.java) to get the raw data.

It compares three different heuristics for calculating dividend payments.

My recommendation is to use

d = (1-l) E[R]

with l being labor share, and E[R] today's expected revenues given price belief and goods for sale.