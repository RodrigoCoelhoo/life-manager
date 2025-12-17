import { useState } from "react";
import { TransactionRecurrence } from "../../services/finances/automatic-transactions/automatic-transactions.dto";

interface RecurrenceSelectProps {
	value: TransactionRecurrence;
	onChange: (value: TransactionRecurrence) => void;
}

export default function RecurrenceSelect({ value, onChange }: RecurrenceSelectProps) {
	const [open, setOpen] = useState(false);

	const toggle = () => setOpen((o) => !o);
	const selectRecurrence = (recurrence: TransactionRecurrence) => {
		onChange(recurrence);
		setOpen(false);
	};

	return (
		<div className="relative inline-block w-full form-input m-0 p-0">
			<button
				type="button"
				onClick={toggle}
				className="w-full flex items-center justify-between transition p-2"
			>
				<span>{value}</span>

				<svg
					className={`h-4 w-4 transition-transform ${open ? "" : "-rotate-90"}`}
					fill="none"
					stroke="currentColor"
					viewBox="0 0 24 24"
				>
					<path
						strokeLinecap="round"
						strokeLinejoin="round"
						strokeWidth={2}
						d="M19 9l-7 7-7-7"
					/>
				</svg>
			</button>

			{open && (
				<div className="absolute mt-1 w-full bg-foreground border border-primary rounded-lg shadow-lg max-h-48 overflow-y-auto z-20">
					{Object.values(TransactionRecurrence).map((recurrence) => (
						<div
							key={recurrence}
							onClick={() => selectRecurrence(recurrence)}
							className="flex items-center px-3 py-2 cursor-pointer hover:bg-background text-sm"
						>
							<span className="font-extralight">{recurrence}</span>
						</div>
					))}
				</div>
			)}
		</div>
	);
}
