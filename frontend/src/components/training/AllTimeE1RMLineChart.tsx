import {
	LineChart,
	Line,
	XAxis,
	YAxis,
	Tooltip,
	ResponsiveContainer,
	Legend,
	CartesianGrid,
} from "recharts";

export interface E1RMPoint {
	label: string; // "YYYY-MM"
	e1rm: number;
}

interface AllTimeE1RMLineChartProps {
	data?: E1RMPoint[];
	lineColor?: string;
	yAxisLabel?: string;
	tooltipFormatter?: (value: number) => string;
	height?: number;
}

export default function AllTimeE1RMLineChart({
	data = [], // default empty
	lineColor = "#A599F0",
	tooltipFormatter,
	height = 300,
}: AllTimeE1RMLineChartProps) {
	// Sort data
	const sortedData = [...data].sort(
		(a, b) => new Date(a.label + "-01").getTime() - new Date(b.label + "-01").getTime()
	);

	// Ensure chart has at least 6 months (fill missing months with 0)
	// Ensure chart has at least 6 months (fill missing months with 0)
	const monthsSet = new Set(sortedData.map((d) => d.label));
	const now = new Date();
	for (let i = 5; i >= 0; i--) {
		const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
		const monthLabel = `${date.getFullYear()}-${(date.getMonth() + 1)
			.toString()
			.padStart(2, "0")}`;
		if (!monthsSet.has(monthLabel)) {
			sortedData.push({ label: monthLabel, e1rm: 0 });
		}
	}


	// Sort again after adding missing months
	sortedData.sort((a, b) => new Date(a.label + "-01").getTime() - new Date(b.label + "-01").getTime());

	return (
		<ResponsiveContainer height={height}>
			<LineChart data={sortedData} margin={{ left: 10, right: 20 }}>
				<CartesianGrid stroke="#2b2b48" fill="#232538" />
				<XAxis
					dataKey="label"
					tickFormatter={(label) => {
						const date = new Date(label + "-01");
						return date.toLocaleString("en-US", { month: "short", year: "numeric" });
					}}
					tick={{ fontSize: 12, fill: "#e0e0e6" }}
					tickMargin={10}
					axisLine={{ stroke: "#6F5DC1", strokeWidth: 1 }}
				/>
				<YAxis
					tick={{ fontSize: 12, fill: "#e0e0e6" }}
					tickMargin={10}
					axisLine={{ stroke: "#6F5DC1", strokeWidth: 1 }}
					tickFormatter={(value) => `${value.toFixed(1)} kg`}
				/>
				<Tooltip
					contentStyle={{
						backgroundColor: "#232538",
						border: "1px solid #A599F0",
						borderRadius: 5,
						padding: "10px",
						fontSize: 12,
					}}
					labelStyle={{ color: "#e0e0e6", fontWeight: "bold" }}
					formatter={(value: number) =>
						value && value > 0
							? tooltipFormatter
								? tooltipFormatter(value)
								: `${value.toFixed(1)} kg`
							: "No data"
					}
				/>
				<Legend
					wrapperStyle={{ fontSize: 15 }}
					iconSize={10}
					formatter={(value) => <span style={{ margin: "0 15px" }}>{value}</span>}
				/>

				<Line
					type="monotone"
					dataKey="e1rm"
					stroke={lineColor}
					strokeWidth={2}
					dot={(props) => (props.payload.e1rm > 0 ? <circle {...props} r={4} /> : null)}
					name="E1RM"
				/>
			</LineChart>
		</ResponsiveContainer>
	);
}
