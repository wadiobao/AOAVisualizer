# AOAVisualizer
AOA Visualizer - Activity on Arrow (AOA) Project Management Visualization
Overview
The AOA Visualizer is a Java-based application that constructs and visualizes an Activity on Arrow (AOA) network diagram to represent project tasks, their dependencies, and scheduling information. This program leverages GraphStream for graph visualization and provides a structured way to determine the earliest and latest possible execution times for tasks in a project.

Features
Graph Visualization: Uses GraphStream to display the AOA network as a directed graph.
Task Dependency Management: Supports manual and pre-defined input methods for task dependencies.
Forward & Backward Pass Calculations:
Calculates Earliest Start (ES) and Earliest Finish (EF) using forward pass.
Determines Latest Start (LS) and Latest Finish (LF) using backward pass.
Slack Time Calculation:
Total Slack (TS): Measures total flexibility in scheduling.
Free Slack (FS): Shows how much a task can be delayed without affecting successors.
Critical Path Identification: Highlights the sequence of tasks that determine the project’s minimum duration.
Dummy Nodes for Complex Dependencies: Handles multiple dependencies with dummy nodes to maintain AOA structure.
Graph Styling: Uses customized styling to differentiate real and dummy nodes and edges.
How It Works
User Input:
The user can choose to use a predefined dataset or enter tasks manually.
Each task requires a name, duration, and preceding tasks.
Graph Construction:
Nodes represent project events.
Directed edges represent activities with durations.
Dummy nodes are added where necessary to ensure correct dependency representation.
Scheduling Calculations:
Earliest & latest start/finish times are computed using a forward and backward pass.
Slack values are calculated to determine flexible and critical tasks.
Graph Display:
The graph is rendered using GraphStream with a SpringBox layout for optimal visualization.
The critical path can be highlighted for project managers.
