import { NutritionalTag } from "../../services/nutrition/ingredient-brand/ingredient-brand.dto";

interface Tag {
	nutrient: string;
	amount: number;
	unit: string;
}

interface Day {
	date: string;
	tags: Tag[];
}

interface WeekOverviewProps {
	weekOverview: Day[];
	currentDate: string;
}

export function WeekView({ weekOverview, currentDate }: WeekOverviewProps) {
	if (!weekOverview) return null;

	const renderDay = (day: Day) => (
		<div
			key={day.date}
			className="w-full h-full p-2 sm:p-3 bg-foreground text-textcolor shadow-md rounded-lg flex flex-col min-h-[150px] sm:min-h-[200px]"
		>
			{/* Date */}
			<div className="flex justify-center mb-1 sm:mb-2">
				<h2
					className={`text-sm sm:text-lg font-semibold px-2 sm:px-3 py-1 border-b-2 ${day.date === currentDate
						? "border-secondary border-b-4 text-secondary"
						: "border-primary"
						}`}
				>
					{day.date.split("-").reverse().join("/")}
				</h2>
			</div>

			{/* Tags */}
			{day.tags.length > 0 ? (
				<ul className="flex-1 flex flex-col gap-0.5 overflow-y-auto">
					{day.tags.map((tag, idx) => (
						<li
							key={idx}
							className={`flex justify-between items-center p-0.5 rounded hover:bg-gray-200/20 transition text-xs sm:text-sm 
								${tag.nutrient === NutritionalTag.CALORIES ? "text-secondary border-b border-primary/50" : ""}`}
						>
							<span className="font-medium truncate pr-1">{tag.nutrient}:</span>
							<span className="font-light whitespace-nowrap">
								{tag.amount.toFixed(2)} {tag.unit}
							</span>
						</li>
					))}
				</ul>
			) : (
				<div className="flex-1 flex items-center justify-center text-gray-400 italic text-xs sm:text-sm px-2 text-center">
					No nutrients logged
				</div>
			)}
		</div>
	);

	return (
		<div className="h-full flex flex-col gap-4">
			<div className="flex flex-col lg:flex-row gap-4 flex-1">
				<div className="flex flex-col sm:flex-row gap-4 w-full">
					{weekOverview.slice(0, 2).map(renderDay)}
				</div>
				<div className="flex flex-col sm:flex-row gap-4 w-full">
					{weekOverview.slice(2, 4).map(renderDay)}
				</div>
			</div>

			{/* Second row: 3 days stacked, each taking equal height */}
			<div className="flex flex-col md:flex-row gap-4 flex-1">
				<div className="flex flex-col sm:flex-row gap-4 w-full md:w-2/3">
					{weekOverview.slice(4, 6).map(renderDay)}
				</div>
				<div className="flex flex-row w-full md:w-1/3">
					{weekOverview.slice(6).map(renderDay)}
				</div>
			</div>
		</div>
	);
}