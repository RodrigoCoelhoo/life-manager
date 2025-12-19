import { useState, useEffect } from "react";

interface SearchBarProps {
	value: string;
	onChange: (debouncedValue: string) => void;
	delay?: number;
	placeholder?: string;
	className?: string;
}

export const SearchBar = ({
	value,
	onChange,
	delay = 500,
	placeholder = "Search...",
	className = "form-input w-full",
}: SearchBarProps) => {
	const [input, setInput] = useState(value);

	useEffect(() => {
		const handler = setTimeout(() => {
			onChange(input);
		}, delay);

		return () => clearTimeout(handler);
	}, [input, delay, onChange]);

	return (
		<input
			type="text"
			value={input}
			placeholder={placeholder}
			className={className}
			onChange={(e) => setInput(e.target.value)}
		/>
	);
};
