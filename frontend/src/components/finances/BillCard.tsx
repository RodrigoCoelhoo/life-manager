import { useState } from "react";
import { Modal } from "../common/Modal";
import type { AutomaticTransactionDTO, AutomaticTransactionResponseDTO, TransactionRecurrence } from "../../services/finances/automatic-transactions/automatic-transactions.dto";
import BillForm from "./BillForm";
import { CategoryColors, CategoryIconComponents } from "../../services/finances/transaction/transaction.dto";
import { formatBalance } from "../../services/finances/currencies.type";

interface BillCardProps extends AutomaticTransactionResponseDTO {
	onUpdate: (id: number, data: AutomaticTransactionDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
	onTrigger: (id: number) => Promise<void>;
}

export default function BillCard({ onUpdate, onDelete, onTrigger, ...bill }: BillCardProps) {
	const [isOpen, setIsOpen] = useState(false);
	const [isHovered, setIsHovered] = useState(false);

	const categoryColor = CategoryColors[bill.category as keyof typeof CategoryColors] || "#cccccc";

	function hexToRgba(hex: string, alpha: number) {
		const r = parseInt(hex.slice(1, 3), 16);
		const g = parseInt(hex.slice(3, 5), 16);
		const b = parseInt(hex.slice(5, 7), 16);
		return `rgba(${r}, ${g}, ${b}, ${alpha})`;
	}

	const formatRecurrence = (recurrence : TransactionRecurrence,interval : number) => {
		let result = "Every ";

		switch (recurrence) {
			case "DAILY":
				result += interval === 1 ? "day" : `${interval} days`;
				break;

			case "WEEKLY":
				result += interval === 1 ? "week" : `${interval} weeks`;
				break;

			case "MONTHLY":
				result += interval === 1 ? "month" : `${interval} months`;
				break;

			case "YEARLY":
				result += interval === 1 ? "year" : `${interval} years`;
				break;
		}

		return result;
	};

	const IconComponent = CategoryIconComponents[bill.category];

	return (
		<>
			<div
				className="p-4 bg-foreground h-60 rounded-lg flex flex-col shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary text-textcolor"
				onClick={() => setIsOpen(true)}
				onMouseEnter={() => setIsHovered(true)}
				onMouseLeave={() => setIsHovered(false)}
			>
				<div className="flex flex-row justify-between items-center p-2">
					<div className="flex flex-row gap-4 items-center">
						<div
							className={`p-2 rounded-lg flex items-center justify-center`}
							style={{ backgroundColor: hexToRgba(categoryColor, 0.6) }}
						>
							<IconComponent
								size={24}
								className="text-textcolor"
							/>
						</div>

						<p className="font-extralight">{bill.category.replace(/_/g, " ")}</p>
					</div>

					<span className="bg-secondary/25 rounded-xl px-2 py-1 font-extralight text-textcolor/80">{bill.type}</span>
				</div>

				<div className="p-2 flex flex-col justify-between h-full">
					<div>
						<div className="flex flex-row justify-between items-end gap-2">
							<div className="flex flex-col">
								<h2 className="text-xl">{bill.name}</h2>
								<span className="font-extralight text-textcolor/80 truncate">
									{bill.wallet.name}
								</span>
							</div>

							<div>
								<span className="rounded-xl px-2 py-1 font-extralight text-textcolor/80 text-end">{formatRecurrence(bill.recurrence, bill.interval)}</span>
							</div>
						</div>
					</div>

					<div className="border border-primary/40">

					</div>

					<div className="flex flex-row justify-between items-center">
						<div className="flex flex-col">
							<span className="text-textcolor/80">
								Amount
							</span>

							<div className={`text-lg font-extralight ${bill.type === "EXPENSE" ? "text-[#F87171]" : "text-[#34D399]"}`}>
								{bill.type === "EXPENSE" ? "- " : "+ "}
								{formatBalance(bill.amount)}
							</div>
						</div>

						<div className="flex flex-col text-right">
							{!isHovered &&
								<div className="flex flex-col">
									<span className="text-textcolor/80">Due date:</span>
									<span className="text-textcolor">{new Date(bill.nextTransactionDate).toLocaleDateString()}</span>
								</div>
							}
							{isHovered && <button
								className="mt-1 px-3 py-2 rounded-lg text-sm font-extralight bg-primary/80 hover:bg-primary hover:cursor-pointer"
								onClick={(e) => {
									e.stopPropagation();
									onTrigger(bill.id);
								}}
							>
								Pay early
							</button>}
						</div>
					</div>
				</div>
			</div>

			<Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
				<BillForm
					bill={bill}
					onClose={() => setIsOpen(false)}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>
		</>
	);
}
