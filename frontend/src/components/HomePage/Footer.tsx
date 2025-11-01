import { MapPinIcon, EnvelopeIcon } from '@heroicons/react/24/solid';
import { FaGithub } from 'react-icons/fa';

export default function Footer() {
	return (
		<footer className="bg-foreground mt-16 w-full drop-shadow-[0_4px_6px_rgba(0,0,0,1.0)] text-textcolor px-8 sm:px-20 py-6 flex flex-col sm:flex-row justify-between items-center gap-6">

			<div className="flex flex-col justify-center items-center sm:items-start ">
				<h3 className="font-semibold text-lg mb-2">Contacts</h3>
				<a
					href="https://github.com/RodrigoCoelhoo"
					target="_blank"
					rel="noopener noreferrer"
					className="hover:text-primary transition-colors font-extralight flex items-center gap-2"
				>
					<FaGithub className="h-6 w-6" />
					RodrigoCoelhoo
				</a>
				<a
					href="mailto:rscoelho.dev@gmail.com"
					className="hover:text-primary transition-colors font-extralight flex items-center gap-2 mt-1"
				>
					<EnvelopeIcon className="h-6 w-6" />
					rscoelho.dev@gmail.com
				</a>
			</div>

			<div className="font-light text-md text-center lg:text-left order-3 sm:order-0">
				©2025 Rodrigo Coelho. All rights reserved.
			</div>

			<div className="flex items-center font-light text-md gap-2">
				<MapPinIcon className="h-6 w-6 text-textcolor" />
				<span>Faro, Portugal</span>
			</div>
		</footer>
	);
}
