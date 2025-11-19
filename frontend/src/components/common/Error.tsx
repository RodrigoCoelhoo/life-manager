import { ExclamationTriangleIcon, ArrowPathIcon } from "@heroicons/react/24/outline";

interface ErrorMessageProps {
	title?: string;
	message: string;
	onRetry?: () => void;
}

export default function ErrorMessage({
	title = "Something went wrong",
	message,
	onRetry,
}: ErrorMessageProps) {
	return (
		<div className="flex flex-col items-center justify-center text-center rounded-lg border border-border">

			<div className="text-red-500 mb-4">
				<ExclamationTriangleIcon className="w-24 h-24 animate-pulse" />
			</div>

			<h2 className="text-3xl font-semibold text-textcolor mb-2">
				{title}
			</h2>

			<p className="text-muted-foreground mb-4">{message}</p>

			{onRetry && (
				<button
					onClick={onRetry}
					className="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg hover:bg-hover transition-all group"
				>
					<ArrowPathIcon className="w-5 h-5 group-hover:animate-spin" />
					Try Again
				</button>
			)}
		</div>
	);
}
