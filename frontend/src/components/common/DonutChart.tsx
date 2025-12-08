import { PieChart, Pie, Cell } from "recharts";
import { CategoryColors } from "../../services/finances/transaction/transaction.dto";
import type { CategorySummaryDTO } from "../../services/finances/dashboard/dashboard.dto";
import { useState } from "react";

interface DonutChartProps {
	categories?: CategorySummaryDTO[];
}

export default function DonutChart({ categories = [] }: DonutChartProps) {
	const [activeIndex, setActiveIndex] = useState<number | null>(null);

	// Parse data
	const data = categories.map((e) => {
		const cleaned = e.amount
			.replace(/[^\d.-]/g, "")
			.replace(/,/g, "")
			.trim();

		return {
			name: e.category,
			value: Number(cleaned),
			rawValue: e.amount,
		};
	});

	const hasData = data.length > 0 && data.some((d) => d.value > 0);
	const chartData = hasData ? data : [{ name: "No Data", value: 1 }];

	const handleMouseEnter = (_: any, index: number) => setActiveIndex(index);
	const handleMouseLeave = () => setActiveIndex(null);

	const activeData = activeIndex !== null ? data[activeIndex] : null;

	return (
		<div className="flex flex-col font-extralight w-full">
			<div className="relative w-[250px] aspect-square mx-auto">
				<PieChart width={250} height={250}>
					<Pie
						data={chartData}
						dataKey="value"
						innerRadius={80}
						outerRadius={120}
						paddingAngle={2}
						stroke="none"
						onMouseEnter={handleMouseEnter}
						onMouseLeave={handleMouseLeave}
					>
						{chartData.map((entry) => (
							<Cell
								key={entry.name}
								fill={
									hasData
										? CategoryColors[entry.name as keyof typeof CategoryColors] ?? "#ccc"
										: "#232538"
								}
							/>
						))}
					</Pie>
				</PieChart>

				{!hasData ? (
					<div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-textcolor text-sm text-center">
						No data available
					</div>
				) : (
					activeData && (
						<div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-background px-2 py-2 rounded-xl shadow-lg text-center pointer-events-none min-w-20">
							<div className="flex items-center justify-center gap-2 mb-1">
								<div
									className="w-3 h-3 rounded-full"
									style={{ backgroundColor: CategoryColors[activeData.name] }}
								/>
								<span className="font-semibold text-sm text-textcolor text-nowrap">
									{activeData.name.replace(/_/g, " ")}
								</span>
							</div>
							<div className="text-xs text-textcolor font-medium">
								{activeData.rawValue}
							</div>
						</div>
					)
				)}
			</div>

			{hasData && (
				<div className="flex flex-col gap-3 mt-4 w-full">
					{data.map((entry) => {
						const name = entry.name ?? "UNKNOWN";
						return (
							<div key={name} className="flex items-center gap-3 text-textcolor">
								<div
									className="w-3 h-3 rounded-full"
									style={{ backgroundColor: CategoryColors[name] }}
								/>
								<span className="text-sm flex-1">
									{name.replace(/_/g, " ")}
								</span>
								<span className="text-sm">{entry.rawValue}</span>
							</div>
						);
					})}
				</div>
			)}
		</div>
	);
}
