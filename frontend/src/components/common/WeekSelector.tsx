import { FaArrowRight, FaArrowLeft } from "react-icons/fa6";

interface WeekSelectorProps {
	value: string; // Format: "YYYY-MM-DD"
	onChange: (v: string) => void;
}

export function WeekSelector({ value, onChange }: WeekSelectorProps) {
	const today = new Date();

	const toDate = (v: string) => {
		const [y, m, d] = v.split("-").map(Number);
		return new Date(y, m - 1, d);
	};

	const toFormat = (d: Date) =>
		`${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, "0")}-${d
			.getDate()
			.toString()
			.padStart(2, "0")}`;

	const getWeekStart = (d: Date) => {
		const day = d.getDay(); 
		const diff = day === 0 ? -6 : 1 - day; 
		const monday = new Date(d);
		monday.setDate(d.getDate() + diff);
		monday.setHours(0, 0, 0, 0);
		return monday;
	};

	const getWeekEnd = (d: Date) => {
		const monday = getWeekStart(d);
		const sunday = new Date(monday);
		sunday.setDate(monday.getDate() + 6);
		return sunday;
	};

	const date = value ? toDate(value) : today;
	const weekStart = getWeekStart(date);
	const weekEnd = getWeekEnd(date);

	const prevWeek = () => {
		const prev = new Date(weekStart);
		prev.setDate(prev.getDate() - 7);
		onChange(toFormat(prev));
	};

	const nextWeek = () => {
		const next = new Date(weekStart);
		next.setDate(next.getDate() + 7);
		if (next <= today) onChange(toFormat(next));
	};

	const display = `${weekStart.getDate().toString().padStart(2, "0")}/${(weekStart.getMonth() + 1).toString().padStart(2, "0")
		}/${weekStart.getFullYear()} - ${weekEnd.getDate().toString().padStart(2, "0")}/${(weekEnd.getMonth() + 1).toString().padStart(2, "0")
		}/${weekEnd.getFullYear()}`;

	return (
		<div className="flex items-center justify-center gap-2 text-sm sm:text-lg xl:text-xl text-textcolor">
			<button
				type="button"
				onClick={prevWeek}
				className="hover:bg-gray-400/30 rounded-full disabled:opacity-50 p-2"
			>
				<FaArrowLeft size={14} />
			</button>

			<div className="px-4 py-2 text-center min-w-36 rounded-xl select-none">
				{display}
			</div>

			<button
				type="button"
				onClick={nextWeek}
				disabled={weekEnd >= today}
				className={`hover:bg-textcolor/10 rounded-full p-2 ${weekEnd >= today ? 'text-gray-500' : ''}`}
			>
				<FaArrowRight size={14} />
			</button>
		</div>
	);
}
