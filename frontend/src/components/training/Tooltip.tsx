import React, { useState, useRef, useEffect } from 'react';

interface TooltipProps {
	children: React.ReactElement;
	content: string;
	delay?: number;
	position?: 'top' | 'bottom' | 'left' | 'right';
	disabled?: boolean;
}

export function Tooltip({
	children,
	content,
	delay = 100,
	position = 'top',
	disabled = false,
}: TooltipProps) {
	const [isVisible, setIsVisible] = useState(false);
	const timeoutRef = useRef<NodeJS.Timeout | null>(null);
	const tooltipRef = useRef<HTMLDivElement>(null);

	const clearTimeout = () => {
		if (timeoutRef.current) {
			window.clearTimeout(timeoutRef.current);
			timeoutRef.current = null;
		}
	};

	const showTooltip = () => {
		if (disabled) return;
		clearTimeout();
		timeoutRef.current = setTimeout(() => {
			setIsVisible(true);
		}, delay);
	};

	const hideTooltip = () => {
		clearTimeout();
		setIsVisible(false);
	};

	// Handle click outside and escape key
	useEffect(() => {
		const handleClickOutside = (event: MouseEvent) => {
			if (tooltipRef.current && !tooltipRef.current.contains(event.target as Node)) {
				hideTooltip();
			}
		};

		const handleEscapeKey = (event: KeyboardEvent) => {
			if (event.key === 'Escape') {
				hideTooltip();
			}
		};

		if (isVisible) {
			document.addEventListener('mousedown', handleClickOutside);
			document.addEventListener('keydown', handleEscapeKey);
		}

		return () => {
			document.removeEventListener('mousedown', handleClickOutside);
			document.removeEventListener('keydown', handleEscapeKey);
		};
	}, [isVisible]);

	// Clean up timeout on unmount
	useEffect(() => {
		return () => {
			clearTimeout();
		};
	}, []);

	// Handle mouse leaving the tooltip itself
	const handleTooltipMouseEnter = () => {
		if (disabled) return;
		clearTimeout();
	};

	const handleTooltipMouseLeave = () => {
		hideTooltip();
	};

	const positionStyles = {
		top: {
			bottom: '100%',
			left: '50%',
			transform: 'translateX(-50%)',
			marginBottom: '8px',
		},
		bottom: {
			top: '100%',
			left: '50%',
			transform: 'translateX(-50%)',
			marginTop: '8px',
		},
		left: {
			top: '50%',
			right: '100%',
			transform: 'translateY(-50%)',
			marginRight: '8px',
		},
		right: {
			top: '50%',
			left: '100%',
			transform: 'translateY(-50%)',
			marginLeft: '8px',
		},
	};

	const arrowStyles = {
		top: {
			top: '100%',
			left: '50%',
			transform: 'translateX(-50%) translateY(-50%) rotate(45deg)',
			marginTop: '-4px',
		},
		bottom: {
			bottom: '100%',
			left: '50%',
			transform: 'translateX(-50%) translateY(50%) rotate(45deg)',
			marginBottom: '-4px',
		},
		left: {
			top: '50%',
			left: '100%',
			transform: 'translateX(-50%) translateY(-50%) rotate(45deg)',
			marginLeft: '-4px',
		},
		right: {
			top: '50%',
			right: '100%',
			transform: 'translateX(50%) translateY(-50%) rotate(45deg)',
			marginRight: '-4px',
		},
	};

	return (
		<div className="relative inline-block">
			{React.cloneElement(children, {
				onMouseEnter: showTooltip,
				onMouseLeave: hideTooltip,
				onFocus: showTooltip,
				onBlur: hideTooltip,
			})}

			{isVisible && content && (
				<div
					ref={tooltipRef}
					className="absolute z-50 animate-fadeIn"
					style={positionStyles[position]}
					onMouseEnter={handleTooltipMouseEnter}
					onMouseLeave={handleTooltipMouseLeave}
				>
					<div className="relative">
						<div className="px-3 py-2 text-sm text-white bg-gray-900 rounded-lg shadow-lg whitespace-nowrap">
							{content}
						</div>
						<div
							className="absolute w-3 h-3 bg-gray-900"
							style={arrowStyles[position]}
						/>
					</div>
				</div>
			)}
		</div>
	);
}