import { useMemo } from 'react';
import { Tooltip } from './Tooltip';

interface ConsistencyMapProps {
	dates?: Array<string | Date>;
	/**
	 * Month to display (0-11). If not provided, shows current month.
	 * 0 = January, 1 = February, ..., 11 = December
	 */
	month?: number;
	year?: number;
	colorPalette?: string[];
	squareSize?: number;
	gap?: number;
	showDayLabels?: boolean;
	showMonthLabels?: boolean;
	dateFormat?: Intl.DateTimeFormatOptions;
	getIntensityLevel?: (count: number, maxCount: number) => number;
}

function normalizeDate(date: string | Date): Date {
	const d = new Date(date);
	d.setHours(0, 0, 0, 0);
	return d;
}

function formatKey(date: Date): string {
	return date.toISOString().split('T')[0];
}

export default function ConsistencyMap({
	dates = [],
	month,
	year,
	squareSize = 12,
	gap = 3,
	showDayLabels = false,
	showMonthLabels = true,
	dateFormat = { year: 'numeric', month: 'long', day: 'numeric' },
}: ConsistencyMapProps) {
	const targetDate = useMemo(() => {
		const today = new Date();
		const targetMonth = month !== undefined ? month : today.getMonth();
		const targetYear = year !== undefined ? year : today.getFullYear();
		return new Date(targetYear, targetMonth, 1);
	}, [month, year]);

	const { counts, weeksData } = useMemo(() => {
		const counts: Record<string, number> = {};
		dates.forEach((d) => {
			const key = formatKey(normalizeDate(d));
			counts[key] = (counts[key] || 0) + 1;
		});

		const targetYear = targetDate.getFullYear();
		const targetMonth = targetDate.getMonth();

		const firstDay = new Date(targetYear, targetMonth, 1);
		const lastDay = new Date(targetYear, targetMonth + 1, 0);

		const start = new Date(firstDay);
		start.setDate(firstDay.getDate() - firstDay.getDay());

		const end = new Date(lastDay);
		end.setDate(lastDay.getDate() + (6 - lastDay.getDay()));

		const totalDays = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1;
		const weeks = Math.ceil(totalDays / 7);

		const weeksData: Date[][] = [];
		for (let w = 0; w < weeks; w++) {
			const week: Date[] = [];
			for (let d = 0; d < 7; d++) {
				const date = new Date(start);
				date.setDate(start.getDate() + (w * 7) + d);
				week.push(normalizeDate(date));
			}
			weeksData.push(week);
		}

		const filteredWeeksData: Date[][] = weeksData.filter(week =>
			week.some(day =>
				day.getMonth() === targetMonth &&
				day.getFullYear() === targetYear
			)
		);

		const maxCount = Math.max(0, ...Object.values(counts));
		return { counts, weeksData: filteredWeeksData, maxCount };

	}, [dates, targetDate]);


	const dayLabels = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

	const getTooltipText = (date: Date, count: number) => {
		const formattedDate = date.toLocaleDateString('en-US', dateFormat);
		if (count === 0) return `No session on ${formattedDate}`;
		return `${count} ${count === 1 ? 'session' : 'sessions'} on ${formattedDate}`;
	};

	const getDayClassName = (date: Date) => {
		const today = normalizeDate(new Date());
		const normalizedDate = normalizeDate(date);

		// Check if date is today
		if (normalizedDate.getTime() === today.getTime()) {
			return 'ring-2 ring-primary';
		}

		// Check if date is in target month
		if (date.getMonth() !== targetDate.getMonth() || date.getFullYear() !== targetDate.getFullYear()) {
			return 'opacity-20';
		}

		return '';
	};

	const monthName = targetDate.toLocaleDateString('en-US', {
		month: 'long',
		year: 'numeric'
	});

	return (
		<div className="">
			{showMonthLabels && (
				<div className="text-sm font-medium text-textcolor mb-3">
					{monthName}
				</div>
			)}

			<div className="flex text-[10px] text-textcolor">
				{showDayLabels && (
					<div className="mr-2 flex flex-col justify-between">
						{dayLabels.map((label, i) => (
							<div
								key={label}
								className="flex items-center justify-end"
								style={{
									height: squareSize,
									marginBottom: i < dayLabels.length - 1 ? gap : 0
								}}
							>
								{i % 2 === 0 ? label : ''}
							</div>
						))}
					</div>
				)}

				<div className="flex">
					{weeksData.map((week, wIdx) => (
						<div
							key={wIdx}
							className="flex flex-col"
							style={{
								gap: `${gap}px`,
								marginRight: wIdx < weeksData.length - 1 ? gap : 0
							}}
						>
							{week.map((day, dIdx) => {
								const key = formatKey(day);
								const trained = counts[key] > 0;

								const dayClass = getDayClassName(day);

								return (
									<Tooltip key={`${key}-${dIdx}`} content={getTooltipText(day, counts[key] || 0)}>
										<button
											className={`rounded-sm transition-all hover:scale-110 focus:outline-none focus:ring-2 focus:ring-secondary ${dayClass} ${trained ? (counts[key] > 1 ? 'bg-primary' : 'bg-secondary') : 'bg-gray-400/30'
												}`}
											style={{
												width: squareSize,
												height: squareSize,
												minWidth: squareSize,
												minHeight: squareSize,
											}}
											aria-label={getTooltipText(day, counts[key] || 0)}
										/>
									</Tooltip>
								);
							})}
						</div>
					))}

				</div>
			</div>
		</div>
	);
}