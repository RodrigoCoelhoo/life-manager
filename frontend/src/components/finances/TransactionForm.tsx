import { useRef, useState } from "react";
import type { TransactionDTO, TransactionResponseDTO } from "../../services/finances/transaction/transaction.dto";
import { InputField } from "../common/InputField";
import { descriptionRules } from "../../rules/rules";
import { DateTimeField } from "../common/DateTimeField";
import { ButtonField } from "../common/ButtonField";
import type { WalletResponseDTO } from "../../services/finances/wallet/wallet.dto";
import { Modal } from "../common/Modal";
import { walletService } from "../../services/finances/wallet/wallet.service";
import { SearchList } from "../common/SearchList";
import { MagnifyingGlassIcon } from "@heroicons/react/16/solid";
import type { ExpenseCategory } from "../../services/finances/transaction/transaction.dto";
import CategorySelect from "./CategorySelect";
import { NumericField } from "../common/NumericField";

interface TransactionFormProps {
	transaction?: TransactionResponseDTO;
	onClose: () => void;
	onCreate?: (exercise: TransactionDTO) => Promise<void>;
	onUpdate?: (id: number, data: TransactionDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function TransactionForm({ transaction, onClose, onCreate, onUpdate, onDelete }: TransactionFormProps) {
	const [submitting, setSubmitting] = useState<boolean>(false);
	const [openWalletSearch, setOpenWalletSearch] = useState<boolean>(false);

	const [date, setDate] = useState<string>(transaction?.date || "");
	const [wallet, setWallet] = useState<WalletResponseDTO | undefined>(transaction?.wallet);
	const [category, setCategory] = useState<ExpenseCategory>(transaction?.category || "SALARY");
	const [description, setDescription] = useState<string>(transaction?.description || "");
	const [amount, setAmount] = useState<string>(transaction?.amount.slice(0, transaction?.amount.length - 1) || "0.00");

	const dateRef = useRef<any>(null);
	const walletRef = useRef<any>(null);
	const amountRef = useRef<any>(null);
	const descriptionRef = useRef<any>(null);

	const handleUpdate = async () => {
		if (!onUpdate || !transaction || wallet === undefined) return;
		await onUpdate(transaction.id, { walletId: wallet?.id, amount, description, date, category });
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !transaction) return;
		await onDelete(transaction.id);
		onClose();
	};

	const handleCreate = async () => {
		if (!onCreate || wallet === undefined) return;
		await onCreate({ walletId: wallet?.id, amount, description, date, category });
		onClose();
	};


	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isDateValid = dateRef.current?.validate();
		const isWalletValid = walletRef.current?.validate();
		const isAmountValid = amountRef.current?.validate();
		const isDescriptionValid = descriptionRef.current?.validate();

		if (!isDateValid || !isWalletValid || !isAmountValid || !isDescriptionValid) {
			return;
		}

		setSubmitting(true);
		if (transaction !== undefined) {
			handleUpdate();
		} else {
			handleCreate();
		}

		setSubmitting(false);
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
							Date
						</label>
						<DateTimeField
							ref={dateRef}
							type="date"
							value={date}
							onChange={setDate}
							rules={[val => (!!val ? true : "Date is required")]}
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

					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Description
						</label>
						<InputField
							ref={descriptionRef}
							value={description}
							onChange={setDescription}
							placeholder="Description"
							rules={descriptionRules()}
							multiline={true}
						/>
					</div>

					<div className="flex flex-row gap-2">

						<div className="flex flex-col w-2/3">
							<label htmlFor="date" className="text-sm mb-1">
								Category
							</label>
							<CategorySelect value={category} onChange={setCategory} />
						</div>

						<div className="flex flex-col w-1/3">
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

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						{transaction ? (submitting ? "Saving" : "Save Changes") : (submitting ? "Creating" : "Create")}
					</button>
				</form>

				{transaction &&
					<div className="border-t border-secondary/50">
						<button
							className="form-submit bg-red-500 w-full hover:bg-red-600"
							onClick={handleDelete}
						>
							Delete Transaction
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