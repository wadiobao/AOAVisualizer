package bai1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

// Import thư viện GraphStream để vẽ đồ thị
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class AOAVisualizer {
	
	static class TaskInfo {
        String name;
        int es, ef, ls, lf, duration, ts, fs;

        TaskInfo(String name, int duration) {
            this.name = name;
            this.duration = duration;
            this.es = 0;
            this.ef = 0;
            this.ls = Integer.MAX_VALUE;
            this.lf = Integer.MAX_VALUE;
            this.ts = 0;
            this.fs = 0;
        }
    }
	
	
    public static void main(String[] args) {
        // Sử dụng Swing để hiển thị đồ thị
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        //System.setProperty("org.graphstream.ui", "javax");
        
        // Khởi tạo Scanner để nhập dữ liệu từ người dùng
        Scanner sc = new Scanner(System.in);
        
        // Tạo đồ thị AOA
        Graph graph = new SingleGraph("AOA");
        
     // Cấu hình để tránh chồng lấn cạnh
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Sử dụng thuật toán bố cục để tối ưu vị trí nút và tránh cạnh đè lên nhau
        graph.setAttribute("layout.quality", 4);
        graph.setAttribute("layout.stabilization-limit", 0.9);

        // Danh sách công việc và công việc trước đó
        Map<String, List<String>> dependencies = new LinkedHashMap<>();
        
        // Bảng ánh xạ tên công việc với số thứ tự nút
        Map<String, Integer> taskNumbers = new LinkedHashMap<>();
        
        // Danh sách cạnh (edges) của đồ thị
        List<String[]> edges = new ArrayList<>();
        
        // Bảng ánh xạ dummy node với nút mà nó nối đến
        Map<Integer, Integer> dummyNodes = new HashMap<>();
        
        // Tập hợp các công việc có công việc tiếp theo
        Set<String> tasksWithSuccessors = new HashSet<>();

        // Biến đếm số nút trong đồ thị
        int nodeCount = 1;
        
        // Biến đếm dummy nodes (bắt đầu từ 100 để tránh trùng với node thật)
        int dummyCount = 100;
        
        // Chuỗi lưu các tên công việc có thể sử dụng
        StringBuilder availableTasks = new StringBuilder();
        
        Map<String, TaskInfo> taskInfo = new LinkedHashMap<>();
        
        SpringBox sbLayout = new SpringBox(false);
        graph.addSink(sbLayout);
        sbLayout.addAttributeSink(graph);
        
     // Hỏi người dùng muốn nhập thủ công hay dùng dữ liệu có sẵn
        System.out.println("Chọn cách nhập dữ liệu:");
        System.out.println("1 - Sử dụng dữ liệu có sẵn");
        System.out.println("2 - Nhập thủ công");
        System.out.print("Lựa chọn của bạn: ");
        int choice = sc.nextInt();
        sc.nextLine(); // Đọc bỏ dòng trống

        int n;
		if (choice == 1) {
            // Tạo sẵn 5 công việc
            String[][] sampleTasks = {
            	//Công việc, thời gian thực hiện, công việc trước đó
                {"A", "5", ""},
                {"B", "6", ""},
                {"C", "5", ""},
                {"D", "7", "A"},
                {"E", "8", "B,C"},
                {"F", "12", "D,E"},
                {"G", "7", "E,F"},
                {"H", "8", "F,G"},
                {"I", "8", "G,H"},
            };

            // Số lượng công việc
            n = sampleTasks.length;

            for (int i = 0; i < n; i++) {
                String task = sampleTasks[i][0];
                int duration = Integer.parseInt(sampleTasks[i][1]);
                String input = sampleTasks[i][2];

                availableTasks.append(task).append(" "); // Lưu tên công việc vào danh sách

                // Nếu có công việc trước, tách thành danh sách
                List<String> predecessors = input.isEmpty() ? new ArrayList<>() : Arrays.asList(input.split(","));

                // Lưu vào danh sách phụ thuộc
                dependencies.put(task, predecessors);

                // Ánh xạ công việc với số thứ tự của nó
                taskNumbers.put(task, nodeCount++);

                taskInfo.put(task, new TaskInfo(task, duration));

                // Đánh dấu các công việc có công việc tiếp theo
                tasksWithSuccessors.addAll(predecessors);
            }
        } else {
            // Nhập thủ công
            System.out.print("Nhập số công việc: ");
            n = sc.nextInt();
            sc.nextLine(); // Đọc bỏ dòng trống

            for (int i = 0; i < n; i++) {
                System.out.print("Nhập tên công việc: ");
                String task = sc.nextLine().trim();
                availableTasks.append(task).append(" "); // Lưu tên công việc vào danh sách
                
                System.out.print("Nhập thời gian thực hiện: ");
                int duration = sc.nextInt();
                sc.nextLine();
                
                System.out.print("Nhập công việc trước đó (cách nhau dấu phẩy, nếu không có thì để trống): ");
                String input = sc.nextLine().trim();

                // Nếu có công việc trước, tách thành danh sách
                List<String> predecessors = input.isEmpty() ? new ArrayList<>() : Arrays.asList(input.split(","));
                
                // Lưu vào danh sách phụ thuộc
                dependencies.put(task, predecessors);
                
                // Ánh xạ công việc với số thứ tự của nó
                taskNumbers.put(task, nodeCount++);
                
                taskInfo.put(task, new TaskInfo(task, duration));
                
                // Đánh dấu các công việc có công việc tiếp theo
                tasksWithSuccessors.addAll(predecessors);
            }
        }

        

        // Định nghĩa nút bắt đầu và kết thúc
        int startNode = 0, endNode = nodeCount;
        
        taskInfo.put("start", new TaskInfo("start", 0));
        taskInfo.put("end", new TaskInfo("end", 0));
        
        taskNumbers.put("start", startNode);
        taskNumbers.put("end", endNode);

        // Thêm cạnh từ nút bắt đầu đến các công việc không có công việc trước đó
        for (String task : dependencies.keySet()) {
            if (dependencies.get(task).isEmpty()) {
                edges.add(new String[]{String.valueOf(startNode), String.valueOf(taskNumbers.get(task)), getTaskLabel(availableTasks, task)});
            }
        }

        // Xử lý các công việc có nhiều công việc trước đó
        for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
            String task = entry.getKey();
            List<String> preds = entry.getValue();
            int taskNum = taskNumbers.get(task);

            if (preds.size() == 1) {
                // Nếu chỉ có 1 công việc trước đó, nối trực tiếp
                edges.add(new String[]{String.valueOf(taskNumbers.get(preds.get(0))), String.valueOf(taskNum), getTaskLabel(availableTasks, task)});
            } else if (preds.size() > 1) {
                // Nếu có nhiều công việc trước đó, tạo một dummy node
                int dummy = dummyCount++;
                dummyNodes.put(dummy, taskNum);
                
                // Nối các công việc trước đó vào dummy node
                for (String pred : preds) {
                    edges.add(new String[]{String.valueOf(taskNumbers.get(pred)), String.valueOf(dummy), "dummy"});
                }
                
                // Nối dummy node vào công việc hiện tại
                edges.add(new String[]{String.valueOf(dummy), String.valueOf(taskNum), getTaskLabel(availableTasks, task)});
            }
        }

     // Tạo danh sách cạnh nối đến nút cuối
        List<String[]> endEdges = new ArrayList<>();
        for (String task : dependencies.keySet()) {
            if (!tasksWithSuccessors.contains(task)) {
                endEdges.add(new String[]{String.valueOf(taskNumbers.get(task)), String.valueOf(endNode), getTaskLabel(availableTasks, task)});
            }
        }

        // Chỉ thêm các cạnh nối đến nút cuối nếu có từ 2 cạnh trở lên
        if (endEdges.size() > 1) {
            edges.addAll(endEdges);
        }else if (endEdges.size() == 1) {
            taskNumbers.remove("end"); // Xóa nút cuối nếu chỉ có 1 cạnh nối đến nó
        }


        // Thêm các nút vào đồ thị với vị trí từ trái sang phải
        int xPosition = 0;
        for (int num : taskNumbers.values()) {
            Node nNode = graph.addNode(String.valueOf(num));
            nNode.setAttribute("ui.label", String.valueOf(num));
            nNode.setAttribute("xyz", xPosition * 2, 0, 0); // Xếp từ trái qua phải
            xPosition++;
        }

        // Thêm các dummy node vào đồ thị
        for (int dummy : dummyNodes.keySet()) {
            Node dNode = graph.addNode(String.valueOf(dummy));
            dNode.setAttribute("ui.label", ""); // Dummy node không có nhãn
            dNode.setAttribute("ui.class", "dummy");
            dNode.setAttribute("xyz", xPosition * 2, 0, 0);
            xPosition++;
        }

        // Thiết lập giao diện đồ thị
        graph.setAttribute("ui.stylesheet",
                "node { shape: circle; size: 40px; fill-color: white; " +
                "text-size: 16; stroke-mode: plain; stroke-width: 2px; stroke-color: black; }" +
                "node.dummy { size: 10px; fill-color: gray; }" +
                "node:clicked { fill-color: red; }" +
                "node:selected { fill-color: yellow; }" +
                "node.highlight { fill-color: lightblue; size: 45px; }" +
                "edge { text-size: 14; text-alignment: above; fill-color: black; arrow-shape: arrow; }" +
                "edge.dummy { stroke-mode: dashes; fill-color: gray; arrow-shape: arrow; }" +
                "edge.highlight { fill-color: blue; size: 3px; }");


        // Thêm các cạnh vào đồ thị
        for (String[] edge : edges) {
            Edge e = graph.addEdge(edge[0] + "-" + edge[1], edge[0], edge[1], true);
            if (edge[2].isEmpty() || edge[2].equals("dummy")) {
                // Nếu là dummy edge nối đến nút cuối thì đặt tên "end"
                if (edge[1].equals(String.valueOf(endNode))) {
                    e.setAttribute("ui.label", "end");
                } else {
                    e.setAttribute("ui.label", "dummy");
                }
                e.setAttribute("ui.class", "dummy");
            } else {
                e.setAttribute("ui.label", edge[2]);
            }
        }
        
     // Tính toán ES, EF (Forward Pass)
        for (String task : dependencies.keySet()) {
            List<String> preds = dependencies.get(task);
            TaskInfo current = taskInfo.get(task);

            if (preds.isEmpty()) {
                current.es = 0;
            } else {
                int maxEF = 0;
                for (String pred : preds) {
                    maxEF = Math.max(maxEF, taskInfo.get(pred).ef);
                }
                current.es = maxEF;
            }
            current.ef = current.es + current.duration;
        }
        
     // Tính toán LS, LF (Backward Pass)
        int maxEF = 0;
        for (TaskInfo task : taskInfo.values()) {
            maxEF = Math.max(maxEF, task.ef);
        }
        taskInfo.get("end").lf = maxEF;
        taskInfo.get("end").ls = maxEF;

        List<String> reversedTasks = new ArrayList<>(taskInfo.keySet());
        Collections.reverse(reversedTasks);

        for (String task : reversedTasks) {
            List<String> successors = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
                if (entry.getValue().contains(task)) {
                    successors.add(entry.getKey());
                }
            }
            TaskInfo current = taskInfo.get(task);

            if (successors.isEmpty()) {
                current.lf = maxEF;
            } else {
                int minLS = Integer.MAX_VALUE;
                for (String succ : successors) {
                    minLS = Math.min(minLS, taskInfo.get(succ).ls);
                }
                current.lf = minLS;
            }
            current.ls = current.lf - current.duration;
        }
        
     // **Tính TS và FS**
        for (String task : dependencies.keySet()) {
            TaskInfo current = taskInfo.get(task);
            current.ts = current.ls - current.es; // TS = LS - ES

            List<String> successors = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
                if (entry.getValue().contains(task)) {
                    successors.add(entry.getKey());
                }
            }

            if (!successors.isEmpty()) {
                int minES = Integer.MAX_VALUE;
                for (String succ : successors) {
                    minES = Math.min(minES, taskInfo.get(succ).es);
                }
                current.fs = minES - current.ef; // FS = ES(next) - EF(current)
            } else {
                current.fs = current.ts; // Nếu không có successor, FS = TS
            }
        }

        String isGantt = "NO";
        Map<String, String> ganttPoint = new HashMap<String, String>();

      
        System.out.println("\nBảng thời gian:");
        System.out.printf("%-10s %-5s %-5s %-5s %-5s %-8s %-12s %-8s\n", "Công việc", "ES", "EF", "LS", "LF", "Tự do", "Toàn phần","Công việc Gantt");
        for (TaskInfo task : taskInfo.values()) {
        	if(task.name.equals("start")) break;
        	if (!task.name.equals("start") && !task.name.equals("end") && task.ts == 0) {
                isGantt = "YES";
                ganttPoint.put(task.name, isGantt);
                
            }
            System.out.printf("%-10s %-5d %-5d %-5d %-5d %-8d %-12d %-5s\n", 
                              task.name, task.es, task.ef, task.ls, task.lf, task.fs, task.ts,isGantt);
            isGantt = "NO";
        }
        
        System.out.println("Điểm gantt:");
        for (Entry<String, String> entry : ganttPoint.entrySet()) {
			System.out.print(entry.getKey()+",");
		}

     List<String> ganttPath = new ArrayList<>();
     Map<String, Integer> startTimes = new HashMap<>();
     int currentTime = 0;

     // Lấy danh sách các công việc Gantt đã được xác định
     List<String> ganttTasks = new ArrayList<>(ganttPoint.keySet());
     // Sắp xếp theo ES tăng dần
     ganttTasks.sort((a, b) -> taskInfo.get(a).es - taskInfo.get(b).es);

     // Xử lý từng công việc Gantt
     for (String task : ganttTasks) {
         TaskInfo currentTask = taskInfo.get(task);
         
         // Kiểm tra xem tất cả công việc tiền nhiệm đã hoàn thành chưa
         List<String> predecessors = dependencies.get(task);
         if (predecessors != null && !predecessors.isEmpty()) {
             int maxPredEndTime = 0;
             for (String pred : predecessors) {
                 if (startTimes.containsKey(pred)) {
                     maxPredEndTime = Math.max(maxPredEndTime, 
                         startTimes.get(pred) + taskInfo.get(pred).duration);
                 }
             }
             currentTime = Math.max(currentTime, maxPredEndTime);
         }
         
         // Thêm công việc vào đường Gantt
         ganttPath.add(task);
         startTimes.put(task, currentTime);
         currentTime += currentTask.duration;
     }

     // In kết quả
     System.out.println("\nCác công việc Gantt theo thứ tự thực hiện: " + 
         String.join(" -> ", ganttPath));
     System.out.println("Thời gian tối thiểu để hoàn thành dự án: " + currentTime + " đơn vị thời gian");
        
     // Đặt vị trí cố định cho các nút để tránh chồng chéo
        int spacingX = 3; // Khoảng cách theo trục X
        int spacingY = 5; // Khoảng cách theo trục Y
        int yLevel = 0;
        Map<String, Integer> nodeLevels = new HashMap<>();

        for (String task : taskNumbers.keySet()) {
            int nodeId = taskNumbers.get(task);

            // Nếu chưa có mức của nút, gán giá trị
            if (!nodeLevels.containsKey(task)) {
                nodeLevels.put(task, yLevel);
                yLevel += spacingY;
            }

            int xPos = nodeId * spacingX;
            int yPos = nodeLevels.get(task);

            // Đặt tọa độ cho nút
            graph.getNode(String.valueOf(nodeId)).setAttribute("xyz", xPos, yPos, 0);
        }

        for (Map.Entry<Integer, Integer> entry : dummyNodes.entrySet()) {
            int dummyId = entry.getKey();
            int realId = entry.getValue();

            // Đặt tọa độ dummy node cách xa một chút so với cạnh chính
            graph.getNode(String.valueOf(dummyId)).setAttribute("xyz", realId * spacingX, nodeLevels.getOrDefault(realId, 0) + 2, 0);
        }

        
        // Hiển thị đồ thị
        graph.display();
       

        
    }

    /**
     * Hàm lấy nhãn công việc và xóa khỏi danh sách công việc có thể sử dụng.
     * Điều này đảm bảo rằng mỗi công việc chỉ xuất hiện trên một cạnh duy nhất.
     */
    private static String getTaskLabel(StringBuilder availableTasks, String task) {
        int index = availableTasks.indexOf(task);
        if (index != -1) {
            availableTasks.delete(index, index + task.length() + 1); // Xóa công việc khỏi danh sách
            return task;
        }
        return "";
    }
}
