import { forwardRef, useImperativeHandle, type ChangeEvent, useState } from "react";
import type { Rule } from "../../rules/rules";

interface DateTimeFieldProps {
	value: string;
	onChange: (val: string) => void;
	type: "date" | "time";
	placeholder?: string;
	rules?: Rule[];
	disabled?: boolean;
	minToday?: boolean;
}

export const DateTimeField = forwardRef(
	({ value, onChange, type, placeholder, rules = [], disabled, minToday }: DateTimeFieldProps, ref) => {
		const [error, setError] = useState<string | null>(null);

		const validate = () => {
			for (const rule of rules) {
				const result = rule(value);
				if (result !== true) {
					setError(result);
					return false;
				}
			}
			setError(null);
			return true;
		};

		useImperativeHandle(ref, () => ({
			validate
		}));

		const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
			const val = e.target.value;
			onChange(val);

			for (const rule of rules) {
				const result = rule(val);
				if (result !== true) {
					setError(result);
					return;
				}
			}
			setError(null);
		};

		const today = new Date().toISOString().split("T")[0];

		return (
			<div className="flex flex-col w-full">
				<input
					type={type}
					value={value}
					placeholder={placeholder}
					onChange={handleChange}
					min={type === "date" && minToday ? today : undefined}
					className={`form-input w-full ${error ? "border-red-500" : ""} ${disabled ? "cursor-not-allowed" : "cursor-pointer"}`}
					disabled={disabled}
				/>
				{error && <p className="text-red-500 text-sm mt-1">{error}</p>}
			</div>
		);
	}
);
