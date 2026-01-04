import { useEffect, useRef, useState, type RefObject } from 'react';

export function useInView<T extends HTMLElement>(
	options: IntersectionObserverInit = { threshold: 0.2 }
): [RefObject<T>, boolean] {
	const ref = useRef<T>(null);
	const [isVisible, setIsVisible] = useState(false);

	useEffect(() => {
		const observer = new IntersectionObserver(([entry]) => {
			if (entry.isIntersecting) {
				setIsVisible(true);
				observer.unobserve(entry.target);
			}
		}, options);

		if (ref.current) observer.observe(ref.current);

		return () => {
			if (ref.current) observer.unobserve(ref.current);
		};
	}, [ref, options]);

	return [ref, isVisible];
}
