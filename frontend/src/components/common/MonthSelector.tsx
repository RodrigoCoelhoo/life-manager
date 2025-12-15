import { useRef } from "react";
import { FaArrowRight, FaArrowLeft } from "react-icons/fa6";

interface MonthSelectorProps {
	value: string; // Format: "YYYY-MM"
	onChange: (v: string) => void;
}

export function MonthSelector({ value, onChange }: MonthSelectorProps) {
	const now = new Date();
	const current = new Date(now.getFullYear(), now.getMonth(), 1);

	const toDate = (ym: string) => {
		const [y, m] = ym.split("-").map(Number);
		return new Date(y, m - 1, 1);
	};

	const toFormat = (d: Date) =>
		`${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, "0")}`;

	const toDisplay = (d: Date) =>
		d.toLocaleDateString("en-US", { month: "long", year: "numeric" });

	const date = value ? toDate(value) : current;
	const str = toFormat(date);
	const display = toDisplay(date);

	const inputRef = useRef<HTMLInputElement | null>(null);

	const prev = () =>
		onChange(toFormat(new Date(date.getFullYear(), date.getMonth() - 1, 1)));
	const next = () => {
		const nxt = new Date(date.getFullYear(), date.getMonth() + 1, 1);
		if (nxt <= current) onChange(toFormat(nxt));
	};

	// Open native picker reliably
	const openPicker = () => {
		const el = inputRef.current;
		if (!el) return;
		try {
			(el as any).showPicker?.();
			el.click();
		} catch {
			el.click();
		}
	};

	return (
		<div className="flex items-center justify-center gap-2 text-sm sm:text-lg xl:text-xl">
			<button
				type="button"
				onClick={prev}
				className="hover:bg-gray-400/30 rounded-full disabled:opacity-50 p-2"
			>
				<FaArrowLeft size={16} />
			</button>

			<div className="relative">
				<div
					onClick={openPicker}
					role="button"
					aria-label="Select month"
					className="px-4 py-2 text-center min-w-24 cursor-pointer select-none"
				>
					{display}
				</div>

				<input
					ref={inputRef}
					type="month"
					value={str}
					onChange={(e) => {
						const v = e.target.value || toFormat(current);
						if (toDate(v) <= current) onChange(v);
					}}
					max={toFormat(current)}
					className="sr-only"
					aria-hidden
				/>
			</div>

			<button
				type="button"
				onClick={next}
				disabled={date >= current}
				className="hover:bg-gray-400/30 rounded-full disabled:opacity-50 p-2"
			>
				<FaArrowRight size={16} />
			</button>
		</div>
	);
}
