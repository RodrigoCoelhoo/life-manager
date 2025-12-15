import {
	LineChart,
	Line,
	XAxis,
	YAxis,
	Tooltip,
	ResponsiveContainer,
	Legend,
} from "recharts";
import type { CurrencyCode } from "../../services/finances/currencies.type";

export interface TimeSeriesPoint {
	label: string;
	expenses?: number;
	income?: number;
	netBalance?: number;
}

interface NetBalanceLineChartProps {
	data: TimeSeriesPoint[];
	lineColor?: string;
	yAxisLabel?: string;
	tooltipFormatter?: (value: number) => string;
	height?: number;
	currency?: CurrencyCode;
}

export default function NetBalanceLineChart({ data, tooltipFormatter, height = 300, currency = "EUR" }: NetBalanceLineChartProps) {
	const sortedData = [...data].sort(
		(a, b) => new Date(a.label + "-01").getTime() - new Date(b.label + "-01").getTime()
	);

	return (
		<ResponsiveContainer height={height}>
			<LineChart data={sortedData} margin={{ left: 10, right: 20 }}>
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
					tickFormatter={(value) =>
						new Intl.NumberFormat("en-US", {
							style: "currency",
							currency: currency,
						}).format(value)
					}
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
						tooltipFormatter ? tooltipFormatter(value) : value.toString()
					}
				/>
				<Legend
					wrapperStyle={{
						fontSize: 15
					}}
					iconSize={10}
					formatter={(value) => <span style={{ margin: "0 15px" }}>{value}</span>}
				/>

				<Line type="monotone" dataKey="income" stroke="#34D399" strokeWidth={2} dot />
				<Line type="monotone" dataKey="expenses" stroke="#F87171" strokeWidth={2} dot />
				<Line type="monotone" dataKey="netBalance" stroke="#4d7eb3" strokeWidth={2} dot />
			</LineChart>
		</ResponsiveContainer>
	);
};
