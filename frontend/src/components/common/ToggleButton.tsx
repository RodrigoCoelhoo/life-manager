import { useState } from "react";

interface ToggleProps {
	defaultOn?: boolean;
	onToggle?: (value: boolean) => void;
}

export default function ToggleButton({ defaultOn = false, onToggle }: ToggleProps) {
	const [isOn, setIsOn] = useState(defaultOn);

	const toggle = () => {
		const newValue = !isOn;
		setIsOn(newValue);
		onToggle?.(newValue);
	};

	return (
		<>
			<button
				type="button"
				onClick={toggle}
				className={`
					relative w-12 h-6 rounded-full transition-colors border border-secondary shadow-md 
					${isOn ? "bg-primary" : "bg-background"} cursor-pointer
				`}
			>
				<span
					className={`
        			  absolute top-1 left-1 w-4 h-4 bg-white rounded-full transition-transform
        			  ${isOn ? "translate-x-6" : ""}
        			`}
				/>
			</button>
		</>
	);
}
