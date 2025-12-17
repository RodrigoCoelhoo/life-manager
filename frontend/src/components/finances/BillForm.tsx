import { useRef, useState } from "react";
import type { WalletResponseDTO, WalletSimpleResponseDTO } from "../../services/finances/wallet/wallet.dto";
import { InputField } from "../common/InputField";
import { descriptionRules, nameRules } from "../../rules/rules";
import { NumericField } from "../common/NumericField";
import RecurrenceSelect from "./RecurrenceSelect";
import { TransactionRecurrence, type AutomaticTransactionDTO, type AutomaticTransactionResponseDTO } from "../../services/finances/automatic-transactions/automatic-transactions.dto";
import type { ExpenseCategory } from "../../services/finances/transaction/transaction.dto";
import { ButtonField } from "../common/ButtonField";
import { MagnifyingGlassIcon } from "@heroicons/react/16/solid";
import { Modal } from "../common/Modal";
import { SearchList } from "../common/SearchList";
import { walletService } from "../../services/finances/wallet/wallet.service";
import CategorySelect from "./CategorySelect";
import { FaCircleInfo } from "react-icons/fa6";
import { DateTimeField } from "../common/DateTimeField";

interface BillFormProps {
	bill?: AutomaticTransactionResponseDTO;
	onClose: () => void;
	onCreate?: (bill: AutomaticTransactionDTO) => Promise<void>;
	onUpdate?: (id: number, data: AutomaticTransactionDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function BillForm({ bill, onClose, onCreate, onUpdate, onDelete }: BillFormProps) {
	const [submitting, setSubmitting] = useState<boolean>(false);
	const [openWalletSearch, setOpenWalletSearch] = useState<boolean>(false);

	const [name, setName] = useState<string>(bill?.name || "");
	const [description, setDescription] = useState<string>(bill?.description || "");
	const [wallet, setWallet] = useState<WalletSimpleResponseDTO | undefined>(bill?.wallet);
	const [amount, setAmount] = useState<string>(bill?.amount.slice(0, bill?.amount.length - 1) || "0.00");
	const [category, setCategory] = useState<ExpenseCategory>(bill?.category || "SALARY");
	const [recurrence, setRecurrence] = useState<TransactionRecurrence>(bill?.recurrence || "DAILY");
	const [interval, setInterval] = useState<number>(bill?.interval || 1);
	const [nextTransactionDate, setNextTransactionDate] = useState<string>(bill?.nextTransactionDate || new Date().toISOString().slice(0, 10));


	const nameRef = useRef<any>(null);
	const amountRef = useRef<any>(null);
	const walletRef = useRef<any>(null);
	const descriptionRef = useRef<any>(null);
	const nextTransactionDateRef = useRef<any>(null);

	const handleUpdate = async () => {
		if (!onUpdate || !bill || wallet === undefined) return;
		await onUpdate(bill.id, { name, description, walletId: wallet.id, amount, category, recurrence, interval, nextTransactionDate });
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !wallet) return;
		await onDelete(wallet.id);
		onClose();
	};

	const handleCreate = async () => {
		if (!onCreate || wallet === undefined) return;
		await onCreate({ name, description, walletId: wallet.id, amount, category, recurrence, interval, nextTransactionDate });
		onClose();
	};


	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isNameValid = nameRef.current?.validate();
		const isAmountValid = amountRef.current?.validate();
		const isWalletValid = walletRef.current?.validate();
		const isDescriptionValid = descriptionRef.current?.validate();
		const isNextTransactionDateValid = nextTransactionDateRef?.current?.validate();

		if (!isNameValid || !isAmountValid || !isWalletValid || !isDescriptionValid || !isNextTransactionDateValid) {
			return;
		}

		setSubmitting(true);
		if (bill !== undefined) {
			handleUpdate();
		} else {
			handleCreate();
		}

		setSubmitting(false);
	};

	const formatRecurrence = () => {
		let result = "Every ";

		switch (recurrence) {
			case "DAILY":
				result += interval === 1 ? "day." : `${interval} days.`;
				break;

			case "WEEKLY":
				result += interval === 1 ? "week." : `${interval} weeks.`;
				break;

			case "MONTHLY":
				result += interval === 1 ? "month." : `${interval} months.`;
				break;

			case "YEARLY":
				result += interval === 1 ? "year." : `${interval} years.`;
				break;
		}

		return result;
	};


	return (
		<>
			<div className="
			bg-foreground 
			rounded-xl 
			shadow-lg 
			p-12
			flex flex-col
			gap-4
			text-textcolor
			"
			>
				<button
					onClick={onClose}
					className="absolute top-2 right-4 text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
				>
					✕
				</button>

				<form className="flex flex-col space-y-4 w-90 sm:w-110" onSubmit={handleSubmit}>
					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Name
						</label>
						<InputField
							ref={nameRef}
							value={name}
							onChange={setName}
							placeholder="Bill name"
							rules={nameRules()}
						/>
					</div>

					<div className="flex flex-col text-left">
						<label htmlFor="description" className="text-sm mb-1">
							Description
						</label>
						<InputField
							ref={descriptionRef}
							value={description}
							onChange={setDescription}
							placeholder="Bill description"
							rules={descriptionRules()}
							multiline={true}
						/>
					</div>

					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Wallet
						</label>
						<ButtonField
							ref={walletRef}
							value={wallet !== undefined ? wallet.name : ""}
							onClick={() => setOpenWalletSearch(true)}
							icon={<MagnifyingGlassIcon width={16} height={16} />}
							placeholder="Select a wallet"
							rules={[
								(value: string) => (value.trim() !== "" ? true : "Please select a wallet")
							]}
						/>
					</div>

					<div className="flex flex-row gap-2">

						<div className="flex flex-col w-3/5">
							<label htmlFor="date" className="text-sm mb-1">
								Category
							</label>
							<CategorySelect value={category} onChange={setCategory} />
						</div>

						<div className="flex flex-col w-2/5">
							<label htmlFor="date" className="text-sm mb-1">
								Amount
							</label>
							<NumericField
								ref={amountRef}
								value={amount}
								onChange={setAmount}
								rules={[
									(v: string) => !isNaN(Number(v)) || "Invalid number",
									(v: string) => v.trim() !== "" || "Amount is required",
									(v: string) => Number(v) !== 0.00 || "Amount is required"
								]}
								placeholder="0.00"
							/>
						</div>
					</div>

					<div className="flex flex-row gap-2">

						<div className="flex flex-col w-3/5">
							<label htmlFor="date" className="text-sm mb-1">
								Recurrence
							</label>
							<RecurrenceSelect value={recurrence} onChange={setRecurrence} />
							<div className="flex flex-row gap-2 items-center text-gray-300/40 mt-1">
								<FaCircleInfo size={16} />
								<p className="">{formatRecurrence()}</p>
							</div>
						</div>

						<div className="flex flex-col w-2/5">
							<label htmlFor="date" className="text-sm mb-1">
								Interval
							</label>
							<input
								type="number"
								min={1}
								step={1}
								value={interval}
								onChange={(e) => setInterval(Number(e.target.value))}
								className="form-input w-full"
							/>
						</div>
					</div>

					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Next Transaction Date
						</label>
						<DateTimeField
							ref={nextTransactionDateRef}
							type="date"
							value={nextTransactionDate}
							onChange={setNextTransactionDate}
							rules={[val => (!!val ? true : "Date is required")]}
							minToday={true}
						/>
					</div>

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						{bill ? (submitting ? "Saving" : "Save Changes") : (submitting ? "Creating" : "Create")}
					</button>
				</form>

				{bill &&
					<div className="border-t border-secondary/50">
						<button
							className="form-submit bg-red-500 w-full hover:bg-red-600"
							onClick={handleDelete}
						>
							Delete Bill
						</button>
					</div>
				}
			</div>

			<Modal isOpen={openWalletSearch} onClose={() => setOpenWalletSearch(false)}>
				<SearchList<WalletResponseDTO>
					fetchItems={walletService.getAllWallets}
					onSelect={(wallet: WalletResponseDTO) => {
						setWallet(wallet);
						setOpenWalletSearch(false);
					}}
					onClose={() => setOpenWalletSearch(false)}
				/>
			</Modal>
		</>
	);
}